package com.rittmann.common.repositories

import com.rittmann.androidtools.log.log
import com.rittmann.common.datasource.local.PostalCodeDao
import com.rittmann.common.model.PostalCode
import javax.inject.Inject

interface PostalCodeRepository {
    fun keepPostalCode(postalCodes: List<PostalCode>)
    fun getCount(): Int
}

class PostalCodeRepositoryImpl @Inject constructor(
    private val postalCodeDao: PostalCodeDao,
) : PostalCodeRepository {

    override fun keepPostalCode(postalCodes: List<PostalCode>) {
        postalCodes.size.log("Size ")
        return postalCodeDao.insert(postalCodes)
    }

    override fun getCount(): Int {
        return postalCodeDao.getCount()
    }
}