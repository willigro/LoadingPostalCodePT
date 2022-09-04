package com.rittmann.postalcode.ui

import android.os.Bundle
import android.view.View
import androidx.work.WorkInfo
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
        downloadFile()
    }

    private fun setupObservers() {
        viewModel.apply {
            downloadWasAlreadyConclude.observe(viewLifecycleOwner) {
                viewModel.storePostalCode()
            }
        }
    }

    private fun downloadFile() {
        viewModel.download()?.observe(viewLifecycleOwner) {
            when (it.state) {
                WorkInfo.State.RUNNING -> {
                    updateProgress()
                }
                WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                    viewModel.downloadHasFailed()
                }
                else -> {}
            }

            if (it.progress.keyValueMap[DOWNLOAD_STATUS_KEY] == DownLoadFileWorkManager.DownloadStatus.DONE) {
                viewModel.downloadWasConcluded()
                updateProgress()
            }
        }
    }

    // TODO: Refactor it to show something cool
    private fun updateProgress() {
    }
}