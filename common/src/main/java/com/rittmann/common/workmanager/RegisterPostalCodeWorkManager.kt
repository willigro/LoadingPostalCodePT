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
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.rittmann.androidtools.log.log
import com.rittmann.common.mappers.lineStringFromCsvToPostalCodeList
import com.rittmann.common.model.PostalCode
import com.rittmann.common.repositories.PostalCodeRepository
import javax.inject.Inject


// TODO Refactor it
class RegisterPostalCodeWorkManager @Inject constructor(
    applicationContext: Context,
    workerParams: WorkerParameters,
    private val postalCodeRepository: PostalCodeRepository,
) : CoroutineWorker(applicationContext, workerParams) {

    enum class DownloadStatus(val value: Int) {
        DONE(1),
    }

    val notificationId = 456
    private val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    private var channelId: String

    init {
        channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("my_service_2", "My Background Service")
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
        postalCodeRepository.getCount().log("Count on manager")
        if (postalCodeRepository.getCount() == 0) {
            val postalCodes = arrayListOf<PostalCode>()
            csvReader().open(POSTAL_CODE_FILE_PATH) {

                val sequence = readAllAsSequence()
                var i = 0
                for (line in sequence) {
//                i.toString().log()
                    if (i == 0) {
                        i++
                        continue
                    }
                    i++ // using to limit the amount
                    postalCodes.add(line.lineStringFromCsvToPostalCodeList())
//                if (i == 10) break
                }

                postalCodes.size.log("Size csv")
            }

            postalCodeRepository.keepPostalCode(postalCodes)
            val count = postalCodeRepository.getCount()
            count.log("Count ")
        }

        setProgress(workDataOf(DOWNLOAD_STATUS_KEY to DownloadStatus.DONE.value))

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

        builder.setContentTitle("title")
            .setTicker("title")
            .setProgress(100, 0, true)
            .setSmallIcon(android.R.drawable.arrow_down_float)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_PROGRESS)
            .addAction(android.R.drawable.ic_delete, "cancel", cancelPendingIntent)
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