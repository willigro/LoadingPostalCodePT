package com.rittmann.postalcode.ui

import android.os.Bundle
import android.view.View
import androidx.work.WorkInfo
import com.rittmann.androidtools.log.log
import com.rittmann.common.lifecycle.BaseFragmentBinding
import com.rittmann.postalcode.R
import com.rittmann.postalcode.databinding.FragmentPostalCodeBinding
import javax.inject.Inject

class PostalCodeFragment :
    BaseFragmentBinding<FragmentPostalCodeBinding>(R.layout.fragment_postal_code) {

    @Inject
    lateinit var viewModel: PostalCodeViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        viewModel.downloadPostalCodes()
    }

    private fun setupObservers() {
        viewModel.apply {

            downloadPostalCodeWorkInfo.observe(viewLifecycleOwner) {
                handleDownloadPostalCodeWorkInfo(it)
            }

            registerPostalCodeWorkInfo.observe(viewLifecycleOwner) {
                handleRegisterPostalCodeWorkInfo(it)
            }

            downloadWasAlreadyConclude.observe(viewLifecycleOwner) {
                viewModel.storePostalCode()
            }

            storeWasAlreadyConclude.observe(viewLifecycleOwner) {
                "storeWasAlreadyConclude concluded".log()
            }

            observeProgress(this)
            observeProgressPriority(this)
        }
    }

    private fun handleRegisterPostalCodeWorkInfo(it: WorkInfo?) {
        when (it?.state) {
            WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                viewModel.storePostalCodeHasFailed()
            }
            // Triggered when starts and when ends (PeriodicWorker)
            WorkInfo.State.ENQUEUED -> {
                viewModel.storePostalCodeIsEnqueued()
            }
            else -> {}
        }
    }

    private fun handleDownloadPostalCodeWorkInfo(workInfo: WorkInfo?) {
        when (workInfo?.state) {
            WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                viewModel.downloadHasFailed()
            }
            // Triggered when starts and when ends (PeriodicWorker)
            WorkInfo.State.ENQUEUED -> {
                viewModel.downloadPostalCodeIsEnqueued()
            }
            else -> {}
        }
    }
}