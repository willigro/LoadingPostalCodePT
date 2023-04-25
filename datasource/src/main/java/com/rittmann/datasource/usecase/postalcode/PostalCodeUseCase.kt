package com.rittmann.datasource.usecase.postalcode

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.rittmann.androidtools.log.log
import com.rittmann.common.constants.EMPTY_STRING
import com.rittmann.datasource.local.preferences.SharedPreferencesModel
import com.rittmann.datasource.model.PostalCode
import com.rittmann.datasource.repositories.postalcode.PostalCodeRepository
import com.rittmann.datasource.workmanager.DownLoadFileWorkManager
import com.rittmann.datasource.workmanager.RegisterPostalCodeWorkManager
import java.util.UUID
import java.util.Calendar
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface PostalCodeUseCase {
    fun downloadPostalCodes(): LiveData<WorkInfo>
    fun downloadHasFailed()
    fun wasDownloadAlreadyConcluded(): Boolean
    fun storePostalCode(): LiveData<WorkInfo>
    fun storePostalCodeHasFailed()
    fun wasStoreAlreadyConcluded(): Boolean
    fun pagingSource(query: String): LiveData<PagingData<PostalCode>>
}

class PostalCodeUseCaseImpl @Inject constructor(
    private val sharedPreferencesModel: SharedPreferencesModel,
    context: Context,
    private val postalCodeRepository: PostalCodeRepository,
) : PostalCodeUseCase {

    private val workManager = WorkManager.getInstance(context)

    override fun downloadPostalCodes(): LiveData<WorkInfo> {
        Log.i("TESTING", "PostalCodeUseCaseImpl downloadPostalCodes")
        return createOneTimeWorkRequest<DownLoadFileWorkManager>(
            getDownloadPostalCodeNotificationId(),
            WorkerType.DOWNLOAD,
        )
    }

    override fun downloadHasFailed() {
        sharedPreferencesModel.setDownloadPostalCodeNotificationId(EMPTY_STRING)
        sharedPreferencesModel.setDownloadPostalCodePeriodicId(EMPTY_STRING)
    }

    override fun wasDownloadAlreadyConcluded(): Boolean {
        return sharedPreferencesModel.isDownloadConcluded().apply {
            "wasDownloadAlreadyConcluded $this".log()
        }
    }

    override fun storePostalCode(): LiveData<WorkInfo> {
        return createPeriodicWorkRequest(
            RegisterPostalCodeWorkManager::class.java,
            getRegisterPostalCodeNotificationId(),
            WorkerType.REGISTER,
        )
    }

    override fun storePostalCodeHasFailed() {
        sharedPreferencesModel.setRegisterPostalCodeNotificationId(EMPTY_STRING)
        sharedPreferencesModel.setRegisterPostalCodePeriodicId(EMPTY_STRING)
    }

    override fun wasStoreAlreadyConcluded(): Boolean {
        return sharedPreferencesModel.isRegisterPostalCodeConcluded().apply {
            "wasStoreAlreadyConcluded $this".log()
        }
    }

    override fun pagingSource(query: String): LiveData<PagingData<PostalCode>> {
        return postalCodeRepository.pagingSource(query)
    }

    enum class WorkerType {
        DOWNLOAD, REGISTER
    }

    /**
     * Private section
     * */
    private fun createPeriodicWorkRequest(
        workerClass: Class<out ListenableWorker>,
        notificationString: String,
        workerType: WorkerType,
    ): LiveData<WorkInfo> {
        val workRequest = PeriodicWorkRequest.Builder(
            workerClass,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.SECONDS
        ).setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).build()

        /**
        Could not instantiate com.rittmann.common.workmanager.DownLoadFileWorkManager
        java.lang.NoSuchMethodException: com.rittmann.common.workmanager.DownLoadFileWorkManager.<init> [class android.content.Context, class androidx.work.WorkerParameters]
         * */

//        val workRequest =
//            OneTimeWorkRequestBuilder<DownLoadFileWorkManager>().build()
//        workManager.enqueue(workRequest)
        workManager.enqueueUniquePeriodicWork(
            notificationString,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest,
        )
//
        val isRunning = isWorkScheduled(notificationString)

        val id = getPeriodicId(
            workRequest.id,
            workerType,
            isRunning,
        )
//
//        Log.i(
//            "TESTING",
//            "PostalCodeUseCaseImpl createPeriodicWorkRequest id=$id, notificationString=$notificationString"
//        )
        return workManager.getWorkInfoByIdLiveData(
            id
        )
    }

    private inline fun <reified T : ListenableWorker> createOneTimeWorkRequest(
        notificationString: String,
        workerType: WorkerType,
    ): LiveData<WorkInfo> {
        val workRequest = OneTimeWorkRequestBuilder<T>().build()

        workManager.enqueueUniqueWork(
            notificationString,
            ExistingWorkPolicy.KEEP,
            workRequest,
        )

        val isRunning = isWorkScheduled(notificationString)

        val id = getPeriodicId(
            workRequest.id,
            workerType,
            isRunning,
        )

        return workManager.getWorkInfoByIdLiveData(
            id
        )
    }

    private fun getPeriodicId(uuid: UUID, workerType: WorkerType, isRunning: Boolean): UUID {
        Log.i(
            "TESTING",
            "PostalCodeUseCaseImpl getPeriodicId uuid=$uuid, workerType=$workerType, isRunning=$isRunning"
        )
        return when (workerType) {
            WorkerType.DOWNLOAD -> {
                val id = sharedPreferencesModel.getDownloadPostalCodePeriodicId()
                if (id.isEmpty()) {
                    sharedPreferencesModel.setDownloadPostalCodePeriodicId(uuid.toString())
                }

                UUID.fromString(
                    sharedPreferencesModel.getDownloadPostalCodePeriodicId()
                )
            }
            WorkerType.REGISTER -> {
                val id = sharedPreferencesModel.getRegisterPostalCodePeriodicId()
                if (id.isEmpty()) {
                    sharedPreferencesModel.setRegisterPostalCodePeriodicId(uuid.toString())
                }

                UUID.fromString(
                    sharedPreferencesModel.getRegisterPostalCodePeriodicId()
                )
            }
        }
    }

    private fun getDownloadPostalCodeNotificationId(): String {
        var notificationId = sharedPreferencesModel.getDownloadPostalCodeNotificationId()
        if (notificationId.isEmpty()) {
            notificationId = Calendar.getInstance().timeInMillis.toString()
            sharedPreferencesModel.setDownloadPostalCodeNotificationId(notificationId)
        }
        return notificationId + "Download"
    }

    private fun getRegisterPostalCodeNotificationId(): String {
        var notificationId = sharedPreferencesModel.getRegisterPostalCodeNotificationId()
        if (notificationId.isEmpty()) {
            notificationId = Calendar.getInstance().timeInMillis.toString()
            sharedPreferencesModel.setRegisterPostalCodeNotificationId(notificationId)
        }
        return notificationId + "Register"
    }

    private fun isWorkScheduled(tag: String): Boolean {
        Log.i("TESTING", "isWorkScheduled $tag")
        return try {
            val workInfoList: List<WorkInfo?> =
                workManager.getWorkInfosForUniqueWork(tag).get() ?: arrayListOf<WorkInfo>()
            var running = false
            for (workInfo in workInfoList) {
                val state = workInfo?.state
                running = state == WorkInfo.State.RUNNING
            }
            running
        } catch (e: ExecutionException) {
            Log.i("TESTING", "isWorkScheduled ${e.message}")
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            Log.i("TESTING", "isWorkScheduled ${e.message}")
            e.printStackTrace()
            false
        }
    }
}