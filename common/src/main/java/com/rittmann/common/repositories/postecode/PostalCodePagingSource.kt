package com.rittmann.common.repositories.postecode

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.sqlite.db.SimpleSQLiteQuery
import com.rittmann.androidtools.log.log
import com.rittmann.common.datasource.local.PostalCodeDao
import com.rittmann.common.datasource.local.TablePostalCode
import com.rittmann.common.datasource.local.like
import com.rittmann.common.datasource.local.selectAll
import com.rittmann.common.extensions.removeAccents
import com.rittmann.common.model.PostalCode
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

class PostalCodePagingSource @Inject constructor(
    private val postalCodeDao: PostalCodeDao,
    private val query: String,
) : PagingSource<Int, PostalCode>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostalCode> {
        val page = params.key ?: 0

        return try {
            val entities = withContext(Dispatchers.IO) {
                val limit = params.loadSize
                val offset = params.loadSize * page

                var where = ""
                val number = query.filter { it.isDigit() }
                val filter = query.removeAccents()
                    .replace("-", "")
                    .filter { it.isDigit().not() }
                    .split(" ")
                    .filter { it.isNotEmpty() }

                val ext = if (number.isNotEmpty() && number.length <= 3) {
                    // 123
                    number
                } else if (number.isNotEmpty() && number.length > 3) {
                    // 12345 -> 5
                    // 123456 -> 56
                    // 1234567 -> 567
                    number.substring(
                        4,
                        min(7, number.length)
                    )
                } else {
                    ""
                }

                // 123 -> ""
                // 1234 -> 1234
                // 123456 -> 1234 | 56 go to ext
                val num = if (number.isNotEmpty() && number.length > 3) {
                    number.substring(0, min(4, number.length))
                } else {
                    ""
                }

                // it will add cod and/or ext, and they need to be checked separated
                var addP = false
                if ((num.isNotEmpty() || ext.isNotEmpty()) && filter.isNotEmpty()) {
                    where += "("
                    addP = true
                }

                filter.forEachIndexed { index, s ->
                    if (index > 0) where += " AND "
                    where += TablePostalCode.NOME_LOCALIDADE_NORMALIZED.like(s)
                }

                // separating the name from the num and ext
                // I was building with OR, if it ends with AND, i'll keep that just to turns it easier to change
                if (addP) {
                    where += ") AND "
                }

                // if there is not filter then it does not need to add an AND, even with it
                // wont be necessary cause the OR was already added
                if (num.isNotEmpty()) {
                    where += TablePostalCode.NUM_COD_POSTAL.like(num)
                }

                if (ext.isNotEmpty()) {
                    if (num.isNotEmpty()) {
                        where += " AND "
                    }
                    where += TablePostalCode.EXT_COD_POSTAL.like(ext)
                }

                val sqliteQuery = SimpleSQLiteQuery(
                    TablePostalCode.TABLE.selectAll(where) +
                            " LIMIT $limit OFFSET $offset"
                )

                sqliteQuery.sql?.log()

                postalCodeDao.getPagedList(sqliteQuery)
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
