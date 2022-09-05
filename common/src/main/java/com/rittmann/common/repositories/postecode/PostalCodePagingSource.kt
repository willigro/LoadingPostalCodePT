package com.rittmann.common.repositories.postecode

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rittmann.common.datasource.local.PostalCodeDao
import com.rittmann.common.model.PostalCode
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostalCodePagingSource @Inject constructor(
    private val postalCodeDao: PostalCodeDao
) : PagingSource<Int, PostalCode>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostalCode> {
        val page = params.key ?: 0

        return try {
            val entities = withContext(Dispatchers.IO) {
                postalCodeDao.getPagedList(params.loadSize, page * params.loadSize)
            }

            LoadResult.Page(
                data = entities,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (entities.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PostalCode>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
