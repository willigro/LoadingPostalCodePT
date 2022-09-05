package com.rittmann.wtest

import com.rittmann.common.lifecycle.BaseViewModelApp
import com.rittmann.common.navigation.OpenScreen
import com.rittmann.postalcode.ui.list.PostalCodeFragment
import javax.inject.Inject

class MainViewModel @Inject constructor() : BaseViewModelApp() {
    fun showPostalCodeScreen() {
        postNavigationEvent(
            OpenScreen(PostalCodeFragment::class.java)
        )
    }
}