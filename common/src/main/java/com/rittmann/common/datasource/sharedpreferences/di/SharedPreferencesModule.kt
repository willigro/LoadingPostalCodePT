package com.rittmann.common.datasource.sharedpreferences.di

import android.app.Application
import com.rittmann.common.datasource.sharedpreferences.SharedPreferencesModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SharedPreferencesModule {

    @Provides
    @Singleton
    fun providesSharedPreferences(application: Application): SharedPreferencesModel {
        return SharedPreferencesModel(application)
    }
}