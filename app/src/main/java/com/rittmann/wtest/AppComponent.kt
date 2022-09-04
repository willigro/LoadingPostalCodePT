package com.rittmann.wtest

import android.app.Application
import androidx.work.WorkManager
import com.rittmann.common.datasource.local.RoomModule
import com.rittmann.common.datasource.network.di.PostalCodeApiNetworkModule
import com.rittmann.common.datasource.sharedpreferences.di.SharedPreferencesModule
import com.rittmann.common.lifecycle.DispatcherProvider
import com.rittmann.postalcode.ui.di.PostalCodeModuleBuilder
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, RoomModule::class, PostalCodeApiNetworkModule::class, SharedPreferencesModule::class, MainModule::class, PostalCodeModuleBuilder::class])
interface AppComponent : AndroidInjector<DaggerApplication> {
    fun inject(application: WTestApplication)

    fun inject(dispatcherProvider: DispatcherProvider)

    fun inject(workManager: WorkManager)

    override fun inject(instance: DaggerApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun dispatcherProvider(dispatcherProvider: DispatcherProvider): Builder

        @BindsInstance
        fun workManager(workManager: WorkManager): Builder

        fun build(): AppComponent
    }
}