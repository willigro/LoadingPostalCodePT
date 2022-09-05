package com.rittmann.common.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rittmann.common.model.PostalCode

@Dao
interface PostalCodeDao {

    @Query("SELECT COUNT(${TablePostalCode.ID}) FROM ${TablePostalCode.TABLE}")
    fun getCount(): Int

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(postalCodes: List<PostalCode>)

    @Query("SELECT * FROM ${TablePostalCode.TABLE} ORDER BY ${TablePostalCode.ID} ASC LIMIT :limit OFFSET :offset")
    fun getPagedList(limit: Int, offset: Int): List<PostalCode>
}