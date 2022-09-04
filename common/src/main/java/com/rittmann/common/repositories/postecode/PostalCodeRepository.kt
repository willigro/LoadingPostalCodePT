package com.rittmann.common.repositories.postecode

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
        return postalCodeDao.insert(postalCodes)
    }

    override fun getCount(): Int {
        return postalCodeDao.getCount().apply {
            log("counting: ")
        }
    }
}