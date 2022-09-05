package com.rittmann.common.usecase.postalcode

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.common.util.concurrent.ListenableFuture
import com.rittmann.androidtools.log.log
import com.rittmann.common.constants.EMPTY_STRING
import com.rittmann.common.datasource.sharedpreferences.SharedPreferencesModel
import com.rittmann.common.model.PostalCode
import com.rittmann.common.repositories.postecode.PostalCodeRepository
import com.rittmann.common.workmanager.DownLoadFileWorkManager
import com.rittmann.common.workmanager.RegisterPostalCodeWorkManager
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface PostalCodeUseCase {
    fun downloadPostalCodes(): LiveData<WorkInfo>
    fun downloadHasFailed()
    fun wasDownloadAlreadyConcluded(): Boolean
    fun storePostalCode(): LiveData<WorkInfo>
    fun storePostalCodeHasFailed()
    fun wasStoreAlreadyConcluded(): Boolean
    fun pagingSource(): Flow<PagingData<PostalCode>>
}

class PostalCodeUseCaseImpl @Inject constructor(
    private val sharedPreferencesModel: SharedPreferencesModel,
    private val workManager: WorkManager,
    private val postalCodeRepository: PostalCodeRepository,
) : PostalCodeUseCase {

    override fun downloadPostalCodes(): LiveData<WorkInfo> {
        return createPeriodicWorkRequest(
            DownLoadFileWorkManager::class.java,
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

    override fun pagingSource(): Flow<PagingData<PostalCode>> {
        return postalCodeRepository.pagingSource()
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
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            workerClass,
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.SECONDS
        ).setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).build()

        workManager.enqueueUniquePeriodicWork(
            notificationString,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest,
        )

        val isRunning = isWorkScheduled(notificationString)

        val id = getPeriodicId(
            periodicWorkRequest.id,
            workerType,
            isRunning,
        )
        return workManager.getWorkInfoByIdLiveData(
            id
        )
    }

    private fun getPeriodicId(uuid: UUID, workerType: WorkerType, isRunning: Boolean): UUID {
        "getPeriodicId $uuid, $workerType, $isRunning".log()
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
        }.apply {
            "getPeriodicId $this".log()
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
        val statuses: ListenableFuture<List<WorkInfo?>?> =
            workManager.getWorkInfosForUniqueWork(tag)
        return try {
            var running = false
            val workInfoList: List<WorkInfo?> = statuses.get() ?: arrayListOf<WorkInfo>()
            for (workInfo in workInfoList) {
                val state = workInfo?.state
                running = state == WorkInfo.State.RUNNING
            }
            running
        } catch (e: ExecutionException) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }
}