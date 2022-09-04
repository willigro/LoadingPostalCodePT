package com.rittmann.common.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rittmann.common.model.PostalCode


@Database(
    entities = [PostalCode::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postalCodeDao(): PostalCodeDao
}