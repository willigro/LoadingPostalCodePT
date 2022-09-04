package com.rittmann.common.workmanager.di

import android.app.Application
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.rittmann.common.workmanager.ChildWorkerFactory
import com.rittmann.common.workmanager.DaggerWorkerFactory
import com.rittmann.common.workmanager.DownLoadFileWorkManager
import com.rittmann.common.workmanager.RegisterPostalCodeWorkManager
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

@Module
interface WorkerModule {

    @Module
    companion object {

        @Provides
        @JvmStatic
        fun providesWorkManager(application: Application): WorkManager {
            return WorkManager.getInstance(application)
        }
    }

    @Binds
    fun bindWorkManagerFactory(factory: DaggerWorkerFactory): WorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(RegisterPostalCodeWorkManager::class)
    fun bindRegisterPostalCodeWorkManager(factory: RegisterPostalCodeWorkManager.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(DownLoadFileWorkManager::class)
    fun bindDownLoadFileWorkManager(factory: DownLoadFileWorkManager.Factory): ChildWorkerFactory
}