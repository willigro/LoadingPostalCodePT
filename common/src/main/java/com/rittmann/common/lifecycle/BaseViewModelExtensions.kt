package com.rittmann.common.lifecycle

import androidx.lifecycle.viewModelScope
import com.rittmann.baselifecycle.base.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * TODO: the dispatcher needs to be injected, in this case a "Dispatcher Provider" when in test every dispatcher are going to be MAIN
 * */
fun BaseViewModel.async(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    execute: suspend () -> Unit
) =
    viewModelScope.launch {
        execute()
    }