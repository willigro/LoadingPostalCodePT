package com.rittmann.wtest.main

import com.rittmann.common.lifecycle.BaseViewModelApp
import com.rittmann.common.navigation.OpenScreen
import com.rittmann.postalcode.ui.list.PostalCodeFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModelApp() {
    fun showPostalCodeScreen() {
        postNavigationEvent(
            OpenScreen(PostalCodeFragment::class.java)
        )
    }
}