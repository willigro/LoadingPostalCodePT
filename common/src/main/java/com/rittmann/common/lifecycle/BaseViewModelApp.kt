package com.rittmann.common.lifecycle

import androidx.lifecycle.viewModelScope
import com.rittmann.baselifecycle.base.BaseViewModel
import com.rittmann.common.liveevent.LiveEvent
import com.rittmann.common.liveevent.MutableLiveEvent
import com.rittmann.common.navigation.NavigationEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 * Copy from Robbie
 * @author Willi
 *
 * */
open class BaseViewModelApp(
    var dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) : BaseViewModel() {

    private val _navEvents = MutableLiveEvent<NavigationEvent>()
    val navigationEvents: LiveEvent<NavigationEvent> = _navEvents

    protected fun postNavigationEvent(event: NavigationEvent) {
        _navEvents.postEvent(event)
    }

    protected fun executeAsync(
        dispatcherProvider: DispatcherProvider = this.dispatcherProvider,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        scope: CoroutineScope = viewModelScope,
        block: suspend () -> Unit
    ) {
        val dp = when (dispatcher) {
            Dispatchers.IO -> dispatcherProvider.io()
            Dispatchers.Main -> dispatcherProvider.main()
            Dispatchers.Default -> dispatcherProvider.default()
            Dispatchers.Unconfined -> dispatcherProvider.unconfined()
            else -> dispatcherProvider.io()
        }
        (viewModelScopeGen ?: scope).launch(dp) {
            block()
        }
    }

    fun <T> executeAsyncThenMain(
        io: () -> T,
        main: (result: T) -> Unit,
        progress: Boolean = false
    ) {
        if (progress)
            showProgress()

        executeAsync {
            val result = io()
            withContext(dispatcherProvider.main()) {
                main(result)

                if (progress)
                    hideProgress()
            }
        }
    }

    fun <T> executeAsyncThenMainSuspend(
        progress: Boolean = false,
        io: suspend () -> T,
        main: (result: T) -> Unit
    ) {
        if (progress)
            showProgress()
        executeAsync {
            val result = io()

            withContext(dispatcherProvider.main()) {
                main(result)

                if (progress)
                    hideProgress()
            }
        }
    }
}

interface DispatcherProvider {

    fun main(): CoroutineDispatcher = Dispatchers.Main
    fun default(): CoroutineDispatcher = Dispatchers.Default
    fun io(): CoroutineDispatcher = Dispatchers.IO
    fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}

class DefaultDispatcherProvider : DispatcherProvider
