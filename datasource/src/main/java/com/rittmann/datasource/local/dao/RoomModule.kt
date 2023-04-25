package com.rittmann.datasource.local.dao

import android.app.Application
import androidx.room.Room
import com.rittmann.common.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Singleton
    @Provides
    fun providesRoomDatabase(mApplication: Application): AppDatabase {
        return Room.databaseBuilder(mApplication, AppDatabase::class.java, BuildConfig.BASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providesProductDao(appDatabase: AppDatabase): PostalCodeDao {
        return appDatabase.postalCodeDao()
    }
}