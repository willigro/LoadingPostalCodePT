package com.rittmann.common.workmanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.rittmann.androidtools.log.log
import com.rittmann.common.R
import com.rittmann.common.constants.EMPTY_STRING
import com.rittmann.common.datasource.sharedpreferences.SharedPreferencesModel
import com.rittmann.common.mappers.lineStringFromCsvToPostalCodeList
import com.rittmann.common.model.PostalCode
import com.rittmann.common.repositories.postecode.PostalCodeRepository
import javax.inject.Inject
import javax.inject.Provider

class RegisterPostalCodeWorkManager(
    applicationContext: Context,
    workerParams: WorkerParameters,
    private val postalCodeRepository: PostalCodeRepository,
    private val sharedPreferencesModel: SharedPreferencesModel
) : CoroutineWorker(applicationContext, workerParams) {

    class Factory @Inject constructor(
        private val repository: Provider<PostalCodeRepository>,
        private val sharedPreferencesModel: Provider<SharedPreferencesModel>,
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return RegisterPostalCodeWorkManager(
                appContext,
                params,
                repository.get(),
                sharedPreferencesModel.get(),
            )
        }
    }

    private val notificationId = 456
    private val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    private var channelId: String

    init {
        channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                RegisterPostalCodeWorkManager::class.java.name,
                "My Background Service"
            )
        } else {
            ""
        }
    }

    override suspend fun doWork(): Result {
        setForeground(
            ForegroundInfo(notificationId, createNotification())
        )

        /*
        * Im going to simply verify the registered amount and if it is more than 0
        * I'll take it as everything was registered, just for simplicity
        * */
        if (postalCodeRepository.getCount() == 0) {
            val postalCodes = arrayListOf<PostalCode>()
            csvReader().open(POSTAL_CODE_FILE_PATH) {

                val sequence = readAllAsSequence()
                var i = 0
                for (line in sequence) {
                    if (i == 0) {
                        i++
                        continue
                    }
                    i++
                    if (i == 40_000) break
                    postalCodes.add(line.lineStringFromCsvToPostalCodeList())
                }
            }

            "going to keep".log()
            postalCodeRepository.keepPostalCode(postalCodes)

            "went to keep".log()
            // Check as concluded
            sharedPreferencesModel.registerPostalCodeWasConcluded()

            "isRegisterPostalCodeConcluded=${sharedPreferencesModel.isRegisterPostalCodeConcluded()}".log()

            // In case of needed, reset the ids to create new notifications and workers
            sharedPreferencesModel.setRegisterPostalCodePeriodicId(EMPTY_STRING)
            sharedPreferencesModel.setRegisterPostalCodeNotificationId(EMPTY_STRING)
        }
        return Result.success()
    }

    /**
     * Create the notification and required channel (O+) for running work
     * in a foreground service.
     */
    private fun createNotification(): Notification {
        val cancelPendingIntent =
            WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(applicationContext, channelId)
        } else {
            Notification.Builder(applicationContext)
        }

        builder
            .setContentTitle(
                applicationContext.getString(R.string.work_manager_register_postal_codes_notification_title)
            )
            .setTicker(
                applicationContext.getString(R.string.work_manager_register_postal_codes_notification_title)
            )
            .setProgress(100, 0, true)
            .setSmallIcon(android.R.drawable.arrow_down_float)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_PROGRESS)
            .addAction(
                android.R.drawable.ic_delete,
                applicationContext.getString(R.string.work_manager_notification_cancel),
                cancelPendingIntent,
            )
        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(chan)
        return channelId
    }

}