package com.rittmann.datasource.local.preferences.di

import android.app.Application
import com.rittmann.datasource.local.preferences.SharedPreferencesModel
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
    fun providesSharedPreferences(application: Application): com.rittmann.datasource.local.preferences.SharedPreferencesModel {
        return com.rittmann.datasource.local.preferences.SharedPreferencesModel(application)
    }
}