package com.rittmann.wtest

import androidx.work.WorkManager
import com.rittmann.common.lifecycle.DefaultDispatcherProvider
import com.rittmann.common.lifecycle.LifecycleApp
import com.rittmann.widgets.dialog.ModalUtil
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class WTestApplication : DaggerApplication(), LifecycleApp {

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
            .workManager(WorkManager.getInstance(applicationContext))
            .build()

        appComponent.inject(this)
        appComponent.inject(DefaultDispatcherProvider())
        appComponent.inject(WorkManager.getInstance(applicationContext))

        return appComponent
    }
}