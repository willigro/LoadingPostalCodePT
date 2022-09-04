package com.rittmann.common.datasource.local

import android.app.Application
import androidx.room.Room
import com.rittmann.common.BuildConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
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