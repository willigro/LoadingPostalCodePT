package com.rittmann.common.workmanager.di

import android.content.Context
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.rittmann.common.workmanager.ChildWorkerFactory
import com.rittmann.common.workmanager.DaggerWorkerFactory
import com.rittmann.common.workmanager.DownLoadFileWorkManager
import com.rittmann.common.workmanager.RegisterPostalCodeWorkManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object WorkerModule : Initializer<WorkManager> {
//
//    @Provides
//    @Singleton
//    override fun create(@ApplicationContext context: Context): WorkManager {
//        val configuration = Configuration.Builder().build()
//        WorkManager.initialize(context, configuration)
//        return WorkManager.getInstance(context)
//    }
//
//    override fun dependencies(): List<Class<out Initializer<*>>> {
//        // No dependencies on other libraries.
//        return emptyList()
//    }
//}