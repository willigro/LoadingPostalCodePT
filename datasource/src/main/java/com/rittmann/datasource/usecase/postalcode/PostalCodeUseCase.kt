package com.rittmann.datasource.usecase.postalcode

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.rittmann.common.constants.EMPTY_STRING
import com.rittmann.common.tracker.track
import com.rittmann.datasource.local.preferences.SharedPreferencesModel
import com.rittmann.datasource.model.PostalCode
import com.rittmann.datasource.repositories.postalcode.PostalCodeRepository
import com.rittmann.datasource.workmanager.DownLoadFileWorkManager
import com.rittmann.datasource.workmanager.RegisterPostalCodeWorkManager
import java.util.*
import java.util.concurrent.ExecutionException
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

    override fun downloadPostalCodes(): LiveData<WorkInfo> = track<LiveData<WorkInfo>> {
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
        return sharedPreferencesModel.isDownloadConcluded()
    }

    override fun storePostalCode(): LiveData<WorkInfo> = track<LiveData<WorkInfo>> {
        return createOneTimeWorkRequest<RegisterPostalCodeWorkManager>(
            getRegisterPostalCodeNotificationId(),
            WorkerType.REGISTER,
        )
    }

    override fun storePostalCodeHasFailed() {
        sharedPreferencesModel.setRegisterPostalCodeNotificationId(EMPTY_STRING)
        sharedPreferencesModel.setRegisterPostalCodePeriodicId(EMPTY_STRING)
    }

    override fun wasStoreAlreadyConcluded(): Boolean {
        return sharedPreferencesModel.isRegisterPostalCodeConcluded()
    }

    override fun pagingSource(query: String): LiveData<PagingData<PostalCode>> {
        return postalCodeRepository.pagingSource(query)
    }

    enum class WorkerType {
        DOWNLOAD, REGISTER
    }

    private inline fun <reified T : ListenableWorker> createOneTimeWorkRequest(
        notificationString: String,
        workerType: WorkerType,
    ): LiveData<WorkInfo> {
        val isRunning = isWorkScheduled(notificationString)

        val currentId = getPeriodicId(
            workerType,
            isRunning,
        )

        return if (currentId == null) {
            val workRequest = OneTimeWorkRequestBuilder<T>().build()

            workManager.enqueueUniqueWork(
                notificationString,
                ExistingWorkPolicy.KEEP,
                workRequest,
            )

            val id = getPeriodicId(
                workRequest.id,
                workerType,
                isRunning,
            )

            workManager.getWorkInfoByIdLiveData(
                id
            )
        } else {
            workManager.getWorkInfoByIdLiveData(
                currentId
            )
        }
    }

    private fun getPeriodicId(uuid: UUID, workerType: WorkerType, isRunning: Boolean): UUID =
        track<UUID>("uuid=$uuid, workerType=$workerType, isRunning=$isRunning") {
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

    private fun getPeriodicId(workerType: WorkerType, isRunning: Boolean): UUID? =
        track<UUID>("workerType=$workerType, isRunning=$isRunning") {
            return when (workerType) {
                WorkerType.DOWNLOAD -> {
                    sharedPreferencesModel.getDownloadPostalCodePeriodicId().let {
                        if (it.isEmpty()) null
                        else UUID.fromString(it)
                    }
                }
                WorkerType.REGISTER -> {
                    sharedPreferencesModel.getRegisterPostalCodePeriodicId().let {
                        if (it.isEmpty()) null
                        else UUID.fromString(it)
                    }
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
        track(tag)
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
            track(e)
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            track(e)
            e.printStackTrace()
            false
        }
    }
}