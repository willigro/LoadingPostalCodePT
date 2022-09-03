package com.rittmann.common.workmanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.rittmann.androidtools.log.log
import java.io.File
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.Request

// TODO Refactor it
class DownLoadFileWorkManager(applicationContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(applicationContext, workerParams) {

    val notificationId = 123
    private val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    private var channelId: String

    init {
        channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("my_service", "My Background Service")
        } else {
            ""
        }
    }

    override suspend fun doWork(): Result {
        setForeground(
            ForegroundInfo(notificationId, createNotification(0))
        )

        val okHttpBuilder = OkHttpClient.Builder()
            .connectTimeout(1L, TimeUnit.MINUTES)
            .readTimeout(1L, TimeUnit.MINUTES)

        val okHttpClient = okHttpBuilder.build()

        val request =
            Request.Builder().addHeader("Accept-Encoding", "gzip, deflate, br")
                .url("https://raw.githubusercontent.com/centraldedados/codigos_postais/master/data/codigos_postais.csv")
                .build()

        runCatching {
            val response = okHttpClient.newCall(request).execute()
            val body = response.body
            val responseCode = response.code

            if (responseCode >= HttpURLConnection.HTTP_OK &&
                responseCode < HttpURLConnection.HTTP_MULT_CHOICE &&
                body != null
            ) {
                body.byteStream().apply {
                    val length = body.contentLength()

                    val file =
                        File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/testingPostalCode.csv")
                    file.createNewFile()

                    file.outputStream().use { fileOut ->
                        var bytesCopied = 0
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var bytes = read(buffer)

                        while (bytes >= 0) {
                            fileOut.write(buffer, 0, bytes)
                            bytesCopied += bytes
                            bytes = read(buffer)
                            // TODO change the return to something like Result
                            val progress = (((bytesCopied * 100) / length).toInt())

                            notificationManager.notify(notificationId, createNotification(progress))

                            setProgress(workDataOf("Progress" to progress))
                        }
                    }
                }
            } else {
                // Report the error
            }
        }

        return Result.success()
    }

    /**
     * Create the notification and required channel (O+) for running work
     * in a foreground service.
     */
    private fun createNotification(downloadProgress: Int): Notification {
        val cancelPendingIntent =
            WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(applicationContext, channelId)
        } else {
            Notification.Builder(applicationContext)
        }
        downloadProgress.log()
        builder.setContentTitle("title")
            .setTicker("title")
            .setProgress(100, downloadProgress, false)
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