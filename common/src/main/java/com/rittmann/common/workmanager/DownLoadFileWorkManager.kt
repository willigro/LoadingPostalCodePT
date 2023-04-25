package com.rittmann.common.workmanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.rittmann.common.R
import com.rittmann.common.constants.EMPTY_STRING
import com.rittmann.common.datasource.sharedpreferences.SharedPreferencesModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider
import okhttp3.OkHttpClient
import okhttp3.Request

// TODO: create a named
val POSTAL_CODE_FILE_PATH =
    "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/testingPostalCode.csv"

// TODO: create a named
const val POSTAL_CODE_URL =
//    "https://raw.githubusercontent.com/centraldedados/codigos_postais/master/data/codigos_postais.csv"
    "http://10.0.0.104:8080/files/postalcodes.csv"

@HiltWorker
class DownLoadFileWorkManager @AssistedInject constructor(
    @Assisted applicationContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val sharedPreferencesModel: SharedPreferencesModel
) : CoroutineWorker(applicationContext, workerParams) {

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
        Log.i("TESTING", "DownLoadFileWorkManager channelId=$channelId")
    }

    override suspend fun doWork(): Result {
        Log.i("TESTING", "DownLoadFileWorkManager doWork")

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

        Log.i("TESTING", "making request POSTAL_CODE_URL=$POSTAL_CODE_URL")

        return try {
            val response = okHttpClient.newCall(request).execute()
            val body = response.body
            val responseCode = response.code

            if (responseCode >= HttpURLConnection.HTTP_OK &&
                responseCode < HttpURLConnection.HTTP_MULT_CHOICE &&
                body != null
            ) {
                Log.i("TESTING", "responseCode=$responseCode")
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
                        // Check as concluded
                        sharedPreferencesModel.downloadWasConcluded()

                        // In case of needed, reset the ids to create new notifications and workers
                        sharedPreferencesModel.setDownloadPostalCodeNotificationId(EMPTY_STRING)
                        sharedPreferencesModel.setDownloadPostalCodePeriodicId(EMPTY_STRING)
                    }

                }
                Result.success()
            } else {
                Log.i(
                    "TESTING",
                    "Error responseCode=${response.code}, message=${response.message}, body=${response.body}"
                )
                // Report the error
                Result.failure()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.i("TESTING", "Error e=${e.message}")
            Result.failure()
        }
    }

    /**
     * Create the notification and required channel (O+) for running work
     * in a foreground service.
     */
    private fun createNotification(): Notification {
        Log.i("TESTING", "DownLoadFileWorkManager createNotification")
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
        Log.i("TESTING", "DownLoadFileWorkManager createNotificationChannel $channelId")
        return channelId
    }

}