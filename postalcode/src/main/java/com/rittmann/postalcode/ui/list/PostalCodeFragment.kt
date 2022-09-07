package com.rittmann.postalcode.ui.list

import android.os.Bundle
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkInfo
import com.rittmann.common.components.EditTextSearch
import com.rittmann.common.lifecycle.BaseFragmentBinding
import com.rittmann.common.viewmodel.viewModelProvider
import com.rittmann.postalcode.R
import com.rittmann.postalcode.databinding.FragmentPostalCodeBinding
import javax.inject.Inject
import kotlinx.coroutines.launch

class PostalCodeFragment :
    BaseFragmentBinding<FragmentPostalCodeBinding>(R.layout.fragment_postal_code) {

    private var adapter: PostalCodeAdapter? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @VisibleForTesting
    lateinit var viewModel: PostalCodeViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = viewModelProvider(viewModelFactory)

        setupUi()
        setupObservers()
        viewModel.downloadPostalCodes()
    }

    private fun setupUi() {
        EditTextSearch(
            binding.postalCodeEditFilter
        ) {
            viewModel.loadPostalCodes(it)
        }.start()

        binding.clearFilter.setOnClickListener {
            // it's clearing and reloading even when the query was already cleared, maybe i'll change it
            binding.postalCodeEditFilter.setText("")
        }
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
        adapter = PostalCodeAdapter()
        binding.postalCodeRecycler.adapter = adapter?.withLoadStateFooter(
            PostalCodeLoadStateAdapter()
        )

        viewModel.postalCodes.observe(viewLifecycleOwner) {
            it?.also {
                lifecycleScope.launch {
                    adapter?.submitData(it)
                }
            }
        }

        viewModel.loadPostalCodes("")
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