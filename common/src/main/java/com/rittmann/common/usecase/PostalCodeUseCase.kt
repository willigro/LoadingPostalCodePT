package com.rittmann.common.usecase

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.rittmann.common.constants.EMPTY_STRING
import com.rittmann.common.datasource.sharedpreferences.SharedPreferencesModel
import com.rittmann.common.workmanager.DownLoadFileWorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface PostalCodeUseCase {
    fun download(): LiveData<WorkInfo>
    fun downloadHasFailed()
}

class PostalCodeUseCaseImpl @Inject constructor(
    private val sharedPreferencesModel: SharedPreferencesModel,
//    private val application: Application,
    private val workManager: WorkManager,
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

    override fun downloadHasFailed() {
        sharedPreferencesModel.setNotificationId(EMPTY_STRING)
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