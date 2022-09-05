package com.rittmann.postalcode.ui.list

import android.os.Bundle
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkInfo
import com.rittmann.common.lifecycle.BaseFragmentBinding
import com.rittmann.common.viewmodel.viewModelProvider
import com.rittmann.postalcode.R
import com.rittmann.postalcode.databinding.FragmentPostalCodeBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PostalCodeFragment :
    BaseFragmentBinding<FragmentPostalCodeBinding>(R.layout.fragment_postal_code) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @VisibleForTesting
    lateinit var viewModel: PostalCodeViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = viewModelProvider(viewModelFactory)

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
                setupPostalCodeList()
            }

            observeProgress(this)
            observeProgressPriority(this)
        }
    }

    private fun setupPostalCodeList() {
        val adapter = PostalCodeAdapter()
        binding.postalCodeRecycler.adapter = adapter.withLoadStateFooter(
            PostalCodeLoadStateAdapter()
        )

        lifecycleScope.launch {
            viewModel.pagingSource.collectLatest {
                adapter.submitData(it)
            }
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