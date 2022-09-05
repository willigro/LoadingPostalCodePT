package com.rittmann.wtest

import android.app.Application
import com.rittmann.common.datasource.local.RoomModule
import com.rittmann.common.datasource.network.di.PostalCodeApiNetworkModule
import com.rittmann.common.datasource.sharedpreferences.di.SharedPreferencesModule
import com.rittmann.common.lifecycle.DispatcherProvider
import com.rittmann.common.repositories.di.RepositoriesModuleBuilder
import com.rittmann.common.usecase.di.UseCasesModuleBuilder
import com.rittmann.common.workmanager.di.WorkerModule
import com.rittmann.postalcode.ui.list.PostalCodeModuleBuilder
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        RoomModule::class,
        PostalCodeApiNetworkModule::class,
        SharedPreferencesModule::class,
        RepositoriesModuleBuilder::class,
        UseCasesModuleBuilder::class,
        MainModule::class,
        PostalCodeModuleBuilder::class,
        WorkerModule::class]
)
interface AppComponent : AndroidInjector<DaggerApplication> {
    fun inject(application: WTestApplication)

    fun inject(dispatcherProvider: DispatcherProvider)

    override fun inject(instance: DaggerApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun dispatcherProvider(dispatcherProvider: DispatcherProvider): Builder

        fun build(): AppComponent
    }
}