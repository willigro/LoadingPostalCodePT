package com.rittmann.common.pagination


import android.content.Context
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rittmann.androidtools.log.Clause
import com.rittmann.androidtools.log.log

object PagingUtils {

    /**
     * @param recyclerView Fragment's recycler view
     * @param context Context
     * @param setUpAdapter Callback to implement adapter's setup
     *
     * */
    fun setUpRecyclerView(
        recyclerView: RecyclerView?,
        context: Context?,
        orientation: Int = RecyclerView.VERTICAL,
        setUpAdapter: (adapter: RecyclerView) -> Unit
    ) {
        recyclerView?.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(20)
            context?.let {
                layoutManager = LinearLayoutManager((it), orientation, false)
            }
            setUpAdapter(this)
        }
    }

    /**
     * @param recyclerView Fragment's recycler view
     * @param needsLoading Context
     * @param refreshList Callback to implement list refreshing
     *
     * */
    fun setUpPaging(
        recyclerView: RecyclerView?,
        needsLoading: MutableLiveData<Boolean>,
        firstLoad: MutableLiveData<Boolean>,
        refreshList: () -> Unit
    ) {
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    (recyclerView.layoutManager?.let { it as LinearLayoutManager })?.let {
                        val visItemCount = it.childCount
                        val totItemCount = it.itemCount
                        val pastItemCount = it.findFirstVisibleItemPosition()

                        needsLoading.value?.let { reload ->
                            if (reload) {
                                if (visItemCount + pastItemCount >= totItemCount) {
                                    needsLoading.value = false
                                    firstLoad.value = false
                                    refreshList()
                                }
                            }

                        }
                    }
                } else {
                    (recyclerView.layoutManager?.let { it as LinearLayoutManager })?.let {
                        if (firstLoad.value == true && it.findLastVisibleItemPosition() > 0) {
                            needsLoading.value = false
                            firstLoad.value = false
                            refreshList()
                        }
                    }
                }
            }
        })
    }

    /**
     * @param scroll scroll view
     * @param refreshList Callback to implement list refreshing
     * */
    fun setUpPaging(
        scroll: NestedScrollView?,
        enableRefresh: LiveData<Boolean>? = null,
        layoutManager: LinearLayoutManager,
        onScroll: ((scrollY: Int) -> Unit)? = null,
        refreshList: () -> Unit
    ) {
        scroll?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            onScroll?.invoke(scrollY)

            if (enableRefresh == null || enableRefresh.value!!) {
                if (v.getChildAt(v.childCount - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight)) &&
                        scrollY > oldScrollY
                    ) {

                        "bottom".log(clause = Clause.DEBUG)

                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            "refreshList".log(clause = Clause.DEBUG)
                            refreshList()
                        }
                    }
                }
            }
        })
    }

    /**
     * @param scroll scroll view
     * @param refreshList Callback to implement list refreshing
     * */
    fun setUpPaging(
        scroll: RecyclerView?,
        enableRefresh: LiveData<PageInfo.PageState>? = null,
        onScroll: ((scrollY: Int) -> Unit)? = null,
        refreshList: () -> Unit
    ) {
        scroll?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                onScroll?.invoke(dy)

                if (dy > 0) {
                    (recyclerView.layoutManager?.let { it as LinearLayoutManager })?.let {
                        val visItemCount = it.childCount
                        val totItemCount = it.itemCount
                        val pastItemCount = it.findFirstVisibleItemPosition()

                        enableRefresh?.value?.let { reload ->
                            if (reload == PageInfo.PageState.IDLE) {
                                if (visItemCount + pastItemCount >= totItemCount) {
                                    refreshList()
                                }
                            }
                        }
                    }
                }
            }
        })
    }
}
