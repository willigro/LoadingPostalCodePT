package com.rittmann.datasource.local.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rittmann.datasource.model.PostalCode


@Database(
    entities = [PostalCode::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postalCodeDao(): PostalCodeDao
}