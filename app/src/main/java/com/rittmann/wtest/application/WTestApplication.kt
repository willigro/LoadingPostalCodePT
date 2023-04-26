package com.rittmann.wtest.application

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.rittmann.common.lifecycle.LifecycleApp
import com.rittmann.widgets.dialog.ModalUtil
import com.rittmann.common.R
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WTestApplication : Application(), LifecycleApp, Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        configureModalUtil()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun configureModalUtil() {
        ModalUtil.defaultTitle = getString(R.string.app_name)
    }
}