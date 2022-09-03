package com.rittmann.common.lifecycle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.rittmann.baselifecycle.base.BaseActivity
import com.rittmann.common.navigation.CloseScreen
import com.rittmann.common.navigation.NavigationEvent
import dagger.android.support.DaggerAppCompatDialogFragment

const val FRAGMENT_RESULT_KEY = "RESULT_KEY"
const val FRAGMENT_RESULT_CODE = "RESULT_CODE"

const val FRAGMENT_EXIT_CODE = 1

abstract class BaseFragmentBinding<T : ViewDataBinding>(
    private val resId: Int,
    private val resIdContainer: Int = -1
) :
    DaggerAppCompatDialogFragment() {
    protected lateinit var binding: T

    lateinit var rootView: View

    var isVisibleToUser = false
        private set

    val screenNavigator by lazy {
        (requireActivity() as BaseBindingActivity<*>).screenNavigator
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<T>(inflater, resId, container, false).let {
        binding = it
        rootView = it.root
        it.lifecycleOwner = viewLifecycleOwner
        it.root
    }

    fun showProgress() {
        (requireActivity() as BaseActivity).showProgress()
    }

    fun hideProgress() {
        (requireActivity() as BaseActivity).hideProgress()
    }

    fun observeProgress(viewModelApp: BaseViewModelApp) {
        viewModelApp.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading == true) {
                showProgress()
            } else {
                hideProgress()
            }
        }
    }

    fun observeProgressPriority(viewModelApp: BaseViewModelApp) {
        (requireActivity() as BaseActivity).observeLoadingPriority(viewModelApp)
    }

    @CallSuper
    open fun onVisible() {
        isVisibleToUser = true
    }

    /**
     * Method called when the fragment becomes invisible, because it got removed, hidden or other fragment was added.
     */
    @CallSuper
    open fun onInvisible() {
        isVisibleToUser = false
    }

    open fun startNavigationEvent(event: NavigationEvent) {
        //This is needed to pass the reference to the fragment
        val updatedEvent = if (event is CloseScreen) CloseScreen(
            this,
            event.reason,
            event.behavior,
            event.containerViewId
        ) else event
        screenNavigator.startNavigationEvent(updatedEvent)
    }
}