package com.rittmann.wtest.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.rittmann.common.extensions.gone
import com.rittmann.common.extensions.visible
import com.rittmann.common.lifecycle.BaseBindingActivity
import com.rittmann.common.liveevent.ConsumerObserver
import com.rittmann.common.tracker.track
import com.rittmann.widgets.dialog.modal
import com.rittmann.wtest.R
import com.rittmann.wtest.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseBindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()

    override val screenHolder: Int = R.id.main_container.apply {
        track(this.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObservers()
        setupViews()

        if (arePermissionsGranted()) {
            requestPermissionsGranted()
        }
    }

    private fun setupViews() = binding.apply {
        labelPermissionsAreNeeded.visible()
        buttonGivePermission.visible()

        buttonGivePermission.setOnClickListener {
            requestStoragePermissions()
        }
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
        track()
        viewModel.showPostalCodeScreen()

        binding.apply {
            labelPermissionsAreNeeded.gone()
            buttonGivePermission.gone()

            labelPermissionsWereDenied.gone()
            buttonOpenSettings.gone()
        }
    }

    override fun requestPermissionsDenied() {
        super.requestPermissionsDenied()

        track()
        modal(
            message = getString(com.rittmann.common.R.string.permissions_were_denied_dialog_message),
            show = true,
            onClickConclude = {
                requestStoragePermissions()
            },
            onClickCancel = {
                requestPermissionsDeniedAndBlocked()
            }
        )
    }

    override fun requestPermissionsDeniedAndBlocked() {
        super.requestPermissionsDeniedAndBlocked()

        track()
        binding.apply {
            labelPermissionsAreNeeded.gone()
            buttonGivePermission.gone()

            labelPermissionsWereDenied.apply {
                if (isVisible) return

                visible()
                text = getString(com.rittmann.common.R.string.permissions_were_denied_and_blocked)
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
        track()
    }
}