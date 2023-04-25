package com.rittmann.wtest.main

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.rittmann.common.extensions.gone
import com.rittmann.common.extensions.visible
import com.rittmann.common.lifecycle.BaseBindingActivity
import com.rittmann.common.liveevent.ConsumerObserver
import com.rittmann.widgets.dialog.modal
import com.rittmann.wtest.R
import com.rittmann.wtest.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseBindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()

    override val screenHolder: Int = R.id.main_container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObservers()
        requestPermissions()
    }

    private fun setupObservers() {
        viewModel.apply {
            navigationEvents.observe(this@MainActivity, ConsumerObserver {
                screenNavigator.startNavigationEvent(it)
            })

            observeLoading(this)
            observeLoadingPriority(this)
        }
    }

    override fun requestPermissionsGranted() {
        viewModel.showPostalCodeScreen()

        binding.apply {
            labelPermissionsWereDenied.gone()
            buttonOpenSettings.gone()
        }
    }

    override fun requestPermissionsDenied() {
        super.requestPermissionsDenied()

        // TODO: adjust the reference error
        modal(
            message = getString(R.string.permissions_were_denied_dialog_message),
            ok = true,
            show = true,
            cancelable = true,
            onClickConclude = {
                requestPermissions()
            }
        )
    }

    override fun requestPermissionsDeniedAndBlocked() {
        super.requestPermissionsDeniedAndBlocked()

        binding.apply {
            labelPermissionsWereDenied.apply {
                if (isVisible) return

                visible()
                // TODO: adjust the reference error
                text = getString(R.string.permissions_were_denied_and_blocked)
            }

            buttonOpenSettings.apply {
                if (isVisible) return

                visible()

                // TODO: implement click protect
                setOnClickListener {
                    openSettingsToGrantPermissions()
                }
            }
        }
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {

    }
}