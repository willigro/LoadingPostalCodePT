package com.rittmann.wtest.application

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.rittmann.common.lifecycle.LifecycleApp
import com.rittmann.common.workmanager.DownLoadFileWorkManager
import com.rittmann.widgets.dialog.ModalUtil
import com.rittmann.wtest.R
import dagger.hilt.EntryPoints
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

@HiltAndroidApp
class WTestApplication : Application(), LifecycleApp, Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
//    @Inject
//    lateinit var downLoadFileWorkManagerFactory: DownLoadFileWorkManager.DownLoadFileWorkManagerFactory

    override fun onCreate() {
        super.onCreate()

        configureModalUtil()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        Log.i("TESTING", "getWorkManagerConfiguration")
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
//            .setExecutor(Dispatchers.Default.asExecutor())
//            .setWorkerFactory(EntryPoints.get(this, DownLoadFileWorkManager::class.java).workerFactory)
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun configureModalUtil() {
        ModalUtil.defaultTitle = getString(R.string.app_name)
    }
}