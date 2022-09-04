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

    private var progressStr = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        viewModel.download()
    }

    private fun setupObservers() {
        viewModel.apply {

            downloadPostalCodeWorkInfo.observe(viewLifecycleOwner) {
                progressStr += "downloadPostalCodeWorkInfo ${it.state} - ${it.progress.keyValueMap[DOWNLOAD_STATUS_KEY]}\n"
                when (it.state) {
                    WorkInfo.State.RUNNING -> {
                        updateProgressDownloadPostalCodeFile()
                    }
                    WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                        viewModel.downloadHasFailed()
                    }
                    else -> {}
                }

                if (it.progress.keyValueMap[DOWNLOAD_STATUS_KEY] == DownLoadFileWorkManager.DownloadStatus.DONE.value) {
                    viewModel.downloadWasConcluded()
                    updateProgressDownloadPostalCodeFile()
                }
            }

            registerPostalCodeWorkInfo.observe(viewLifecycleOwner) {
                progressStr += "registerPostalCodeWorkInfo ${it.state} - ${it.progress.keyValueMap[DOWNLOAD_STATUS_KEY]}\n"
                when (it.state) {
                    WorkInfo.State.RUNNING -> {
                        updateProgressRegisterPostalCodeFile()
                    }
                    WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                        viewModel.storePostalCodeHasFailed()
                    }
                    // Triggered when starts and when ends
                    WorkInfo.State.ENQUEUED -> {
                        viewModel.checkIfStoreWasConclude()
                    }
                    else -> {}
                }
            }
            downloadWasAlreadyConclude.observe(viewLifecycleOwner) {
                viewModel.storePostalCode()
                // TODO update UI
            }

            storeWasAlreadyConclude.observe(viewLifecycleOwner) {
                "storeWasAlreadyConclude concluded".log()
            }
        }
    }

    // TODO: Refactor it to show something cool
    private fun updateProgressDownloadPostalCodeFile() {
        binding.progress.text = progressStr
    }

    private fun updateProgressRegisterPostalCodeFile() {
        binding.progress.text = progressStr
    }
}