package com.rittmann.common.usecase

import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.rittmann.androidtools.log.log
import com.rittmann.common.constants.EMPTY_STRING
import com.rittmann.common.datasource.sharedpreferences.SharedPreferencesModel
import com.rittmann.common.repositories.PostalCodeRepository
import com.rittmann.common.workmanager.DownLoadFileWorkManager
import com.rittmann.common.workmanager.RegisterPostalCodeWorkManager
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface PostalCodeUseCase {
    fun download(): LiveData<WorkInfo>
    fun downloadConcluded()
    fun downloadHasFailed()
    fun downloadWasAlreadyConcluded(): Boolean
    fun storePostalCode(): LiveData<WorkInfo>
}

class PostalCodeUseCaseImpl @Inject constructor(
    private val sharedPreferencesModel: SharedPreferencesModel,
    private val workManager: WorkManager,
    private val postalCodeRepository: PostalCodeRepository,
) : PostalCodeUseCase {

    override fun download(): LiveData<WorkInfo> {
//        val workManager = WorkManager.getInstance(application)
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            DownLoadFileWorkManager::class.java, // TODO: think about DI
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.SECONDS
        ).setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).build()

        workManager.enqueueUniquePeriodicWork(
            getNotificationId(),
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )

        return workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
    }

    override fun downloadConcluded() {
        sharedPreferencesModel.downloadWasConcluded()
    }

    override fun downloadHasFailed() {
        sharedPreferencesModel.setNotificationId(EMPTY_STRING)
    }

    override fun downloadWasAlreadyConcluded(): Boolean {
        return sharedPreferencesModel.getIsDownloadConcluded().apply {
            "downloadWasAlreadyConcluded=$this".log()
        }
    }

    override fun storePostalCode(): LiveData<WorkInfo> {
        "storePostalCode".log()
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            RegisterPostalCodeWorkManager::class.java, // TODO: think about DI
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.SECONDS
        ).setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).build()

        workManager.enqueueUniquePeriodicWork(
            Calendar.getInstance().timeInMillis.toString(), // TODO Move it so shared
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )

        return workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
    }

    private fun getNotificationId(): String {
        var notificationId = sharedPreferencesModel.getNotificationId()
        if (notificationId.isEmpty()) {
            notificationId = Calendar.getInstance().timeInMillis.toString()
            sharedPreferencesModel.setNotificationId(notificationId)
        }
        return notificationId
    }
}