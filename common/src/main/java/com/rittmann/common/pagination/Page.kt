package com.rittmann.common.pagination

import androidx.lifecycle.MutableLiveData
import com.rittmann.androidtools.log.log

interface PageItem {
    fun getIdentification(): String
}

fun <T : PageItem> List<T>.different(list: List<T>): Boolean {
    "sizes: current $size new ${list.size}".log()
    if (size != list.size) return true

    for (i in 0 until size) {
        "ids: ${this[i].getIdentification()} <-> ${list[i].getIdentification()}".log()
        if (this[i].getIdentification() != list[i].getIdentification()) return true
    }

    return false
}

class SimpleObservable<T>(
    val onChanged: (T?) -> Unit
) {
    var value: T? = null
        set(value) {
            field = value
            onChanged(value)
        }
}

/**
 *
 * SpaceX uses offset, so I'm need to change the PAGE logic for an Offiset logic
 * */

data class PageInfo<T : PageItem>(
    var page: Int = DEFAULT_PAGE_INDEX,
    var size: Int = DEFAULT_PAGE_SIZE,
) {

    private var isEndList: SimpleObservable<Boolean> = SimpleObservable {
        if (it == true)
            enableRefresh.value = PageState.IS_END
    }

    var enableRefresh: MutableLiveData<PageState> = MutableLiveData(PageState.IDLE)
    var completeList: List<T> = arrayListOf()
    var lastList: List<T> = arrayListOf()
    var requestedPage: Int = DEFAULT_PAGE_INDEX

    class PageResult<T : PageItem>(
        var list: List<T> = arrayListOf(),
        var isEndList: Boolean = false
    )

    fun getResult(pageResult: PageResult<T>, toIdle: Boolean = true): List<T> {
        var load = (lastList as List<PageItem>).let {
            if (lastList.different(pageResult.list)) {
                lastList = pageResult.list
                true
            } else {
                this.isEndList.value = true
                false
            }
        }

        addOnlyIfNotContains(pageResult)

        this.isEndList.value = pageResult.isEndList

        if (lastList.size < size) {
            this.isEndList.value = true
            load = false
        }

        if (pageResult.list.isNotEmpty())
            page = requestedPage

        "is to load again: $load".log()
        val result = if (load) completeList else {
            arrayListOf<T>().apply {
                addAll(completeList)
            }
        }

        if (toIdle)
            setEnableRefresh(PageState.IDLE)

        return result
    }

    private fun addOnlyIfNotContains(pageResult: PageResult<T>) {
        completeList.apply {
            for (new in pageResult.list) {
                var found = false
                for (complete in this) {
                    if (new.getIdentification() == complete.getIdentification()) {
                        found = true
                        break
                    }
                }
                if (found.not())
                    (completeList as ArrayList).add(new)
            }
        }
    }

    fun addOnlyIfNotContains(item: T) {
        (completeList as ArrayList).apply {
            var found = false
            for (complete in this) {
                if (item.getIdentification() == complete.getIdentification()) {
                    found = true
                    break
                }
            }
            if (found.not())
                add(item)
        }
    }

    fun getNextPage(next: Boolean = true): PageInfo<T> {
        val p = if (isEndList.value == true) page + lastList.size
        else {
//            if (next) page + 1 else DEFAULT_PAGE_INDEX
//            if (next) page + list.size else DEFAULT_PAGE_INDEX
            if (next) completeList.size else DEFAULT_PAGE_INDEX
        }

        if (p == DEFAULT_PAGE_INDEX)
            reset()

        return this.copy(page = p).also { info ->
            requestedPage = info.page
            "hasPageToLoad, trying load the page ${info.page}".log()
        }
    }

     fun reset() {
        requestedPage = DEFAULT_PAGE_INDEX
        page = DEFAULT_PAGE_INDEX
        (completeList as ArrayList).clear()
        (lastList as ArrayList).clear()

        // I think that it is not a good idea, but for a while I'm going to let it here
        enableRefresh.postValue(PageState.IDLE)
    }

    fun setEnableRefresh(state: PageState) {
        if (enableRefresh.value == PageState.IS_END) return
        enableRefresh.value = state
    }

    companion object {
        const val DEFAULT_PAGE_INDEX = 0 //1
        const val DEFAULT_PAGE_SIZE = 20
    }

    enum class PageState {
        LOADING, IDLE, IS_END
    }
}