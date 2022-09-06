package com.rittmann.common.repositories.postecode

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.rittmann.androidtools.log.log
import com.rittmann.common.datasource.local.PostalCodeDao
import com.rittmann.common.model.PostalCode
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface PostalCodeRepository {
    fun keepPostalCode(postalCodes: List<PostalCode>)
    fun getCount(): Int
    fun pagingSource(query: String): LiveData<PagingData<PostalCode>>
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

    override fun pagingSource(query: String) = Pager(
        PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
    ) {
        PostalCodePagingSource(postalCodeDao, query)
    }.liveData
}