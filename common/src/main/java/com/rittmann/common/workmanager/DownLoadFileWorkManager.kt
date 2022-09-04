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
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.rittmann.common.R
import java.io.File
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import okhttp3.OkHttpClient
import okhttp3.Request

val POSTAL_CODE_FILE_PATH =
    "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/testingPostalCode.csv"

const val POSTAL_CODE_URL =
    "https://raw.githubusercontent.com/centraldedados/codigos_postais/master/data/codigos_postais.csv"

// TODO Refactor it
class DownLoadFileWorkManager(applicationContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(applicationContext, workerParams) {

    class Factory @Inject constructor() : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return DownLoadFileWorkManager(
                appContext,
                params,
            )
        }
    }

    enum class DownloadStatus(val value: Int) {
        DONE(1),
    }

    private val notificationId = 123
    private val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    private var channelId: String

    init {
        channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                DownLoadFileWorkManager::class.java.name,
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

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1L, TimeUnit.MINUTES)
            .readTimeout(1L, TimeUnit.MINUTES)
            .addInterceptor(UnzippingInterceptor())
            .build()

        val request =
            Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept", "*/*")
                .url(POSTAL_CODE_URL)
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
                    val file = File(POSTAL_CODE_FILE_PATH)
                    file.createNewFile()
                    file.outputStream().use { fileOut ->
                        var bytesCopied = 0
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var bytes = read(buffer)

                        while (bytes >= 0) {
                            fileOut.write(buffer, 0, bytes)
                            bytesCopied += bytes
                            bytes = read(buffer)
                        }
                        setProgress(workDataOf(DOWNLOAD_STATUS_KEY to DownloadStatus.DONE.value))
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
                applicationContext.getString(R.string.work_manager_download_postal_codes_notification_title)
            )
            .setTicker(
                applicationContext.getString(R.string.work_manager_download_postal_codes_notification_title)
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