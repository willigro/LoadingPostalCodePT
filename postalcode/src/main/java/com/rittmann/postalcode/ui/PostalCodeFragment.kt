package com.rittmann.postalcode.ui

import android.os.Bundle
import android.view.View
import androidx.work.Data
import androidx.work.WorkInfo
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

        viewModel.download().observe(viewLifecycleOwner) {
            when (it.state) {
                WorkInfo.State.RUNNING -> {
                    updateProgress(it.progress)
                }
                WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                    viewModel.downloadHasFailed()
                }
                else -> {}
            }
        }
    }

    // TODO: Refactor it to show the numeric progress
    private fun updateProgress(progress: Data) {
        binding.progress.text = progress.toString()
    }
}