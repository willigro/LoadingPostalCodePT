package com.rittmann.wtest

import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.rittmann.common.lifecycle.DefaultDispatcherProvider
import com.rittmann.common.lifecycle.LifecycleApp
import com.rittmann.widgets.dialog.ModalUtil
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Inject

class WTestApplication : DaggerApplication(), LifecycleApp {

    @Inject
    lateinit var workerFactory: WorkerFactory

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        configureModalUtil()
    }

    private fun configureModalUtil() {
        ModalUtil.defaultTitle = getString(R.string.app_name)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .dispatcherProvider(DefaultDispatcherProvider())
            .build()

        appComponent.inject(this)
        appComponent.inject(DefaultDispatcherProvider())

        // Initialize the work manager with a custom configuration, allowing to provide variables
        WorkManager.initialize(
            applicationContext,
            Configuration.Builder().setWorkerFactory(workerFactory).build()
        )

        return appComponent
    }
}