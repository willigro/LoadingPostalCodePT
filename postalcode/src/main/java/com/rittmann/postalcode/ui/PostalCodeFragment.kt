package com.rittmann.postalcode.ui

import android.os.Bundle
import android.view.View
import androidx.work.WorkInfo
import com.rittmann.androidtools.log.log
import com.rittmann.common.lifecycle.BaseFragmentBinding
import com.rittmann.common.workmanager.DOWNLOAD_STATUS_KEY
import com.rittmann.common.workmanager.DownLoadFileWorkManager
import com.rittmann.postalcode.R
import com.rittmann.postalcode.databinding.FragmentPostalCodeBinding
import javax.inject.Inject

/**
 * TODO: Save the postal code when:
 *  OK (but im not handling error before save the preferences) - Download is finished
 * TODO: In case of the download had been stopped:
 *  - Save the last index storage and start again when opens the app
 *  - I must keep the total number of postal code to know when I need to stop
 *      - It means that I'll stop and do not download again when the number of items storage is
 *      equals to the postal code items
 * */
class PostalCodeFragment :
    BaseFragmentBinding<FragmentPostalCodeBinding>(R.layout.fragment_postal_code) {

    @Inject
    lateinit var viewModel: PostalCodeViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        startAndObserverThePostalCodeDownload()
    }

    private fun setupObservers() {
        viewModel.apply {
            downloadWasAlreadyConclude.observe(viewLifecycleOwner) {
                startAndObserveTheStorePostalCode()
            }
        }
    }

    private fun startAndObserveTheStorePostalCode() {
        viewModel.storePostalCode()?.observe(viewLifecycleOwner) {
            "storePostalCode=$it".log()
        }
    }

    private fun startAndObserverThePostalCodeDownload() {
        viewModel.download()?.observe(viewLifecycleOwner) {
            it.toString().log()
            when (it.state) {
                WorkInfo.State.RUNNING -> {
                    updateProgress()
                }
                WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                    viewModel.downloadHasFailed()
                }
                else -> {}
            }

            if (it.progress.keyValueMap[DOWNLOAD_STATUS_KEY] == DownLoadFileWorkManager.DownloadStatus.DONE.value) {
                viewModel.downloadWasConcluded()
                updateProgress()
            }
        }
    }

    // TODO: Refactor it to show something cool
    private fun updateProgress() {
    }
}