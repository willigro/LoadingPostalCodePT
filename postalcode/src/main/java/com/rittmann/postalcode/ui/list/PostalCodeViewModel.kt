package com.rittmann.postalcode.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.work.WorkInfo
import com.rittmann.baselifecycle.livedata.SingleLiveEvent
import com.rittmann.common.lifecycle.BaseViewModelApp
import com.rittmann.common.usecase.postalcode.PostalCodeUseCase
import com.rittmann.widgets.progress.ProgressPriorityControl
import javax.inject.Inject

class PostalCodeViewModel @Inject constructor(
    private val postalUseCase: PostalCodeUseCase
) : BaseViewModelApp() {

    private val _storePostalCodeWorkInfo: MediatorLiveData<WorkInfo> = MediatorLiveData()
    val registerPostalCodeWorkInfo: LiveData<WorkInfo> get() = _storePostalCodeWorkInfo

    private val _downloadPostalCodeWorkInfo: MediatorLiveData<WorkInfo> = MediatorLiveData()
    val downloadPostalCodeWorkInfo: LiveData<WorkInfo> get() = _downloadPostalCodeWorkInfo

    private val _downloadWasAlreadyConclude: SingleLiveEvent<Void> = SingleLiveEvent()
    val downloadWasAlreadyConclude: LiveData<Void> get() = _downloadWasAlreadyConclude

    private val _storeWasAlreadyConclude: SingleLiveEvent<Void> = SingleLiveEvent()
    val storeWasAlreadyConclude: LiveData<Void> get() = _storeWasAlreadyConclude

    private val progressModelDownload = ProgressPriorityControl.ProgressModel(id = "download")
    private val progressModelRegister = ProgressPriorityControl.ProgressModel(id = "register")

    fun downloadPostalCodes() {
        showProgress(progressModelDownload)

        if (postalUseCase.wasDownloadAlreadyConcluded()) {
            _downloadWasAlreadyConclude.call()
            hideProgress(progressModelDownload)
            return
        }

        _downloadPostalCodeWorkInfo.addSource(postalUseCase.downloadPostalCodes()) {
            _downloadPostalCodeWorkInfo.value = it
        }
    }

    fun downloadPostalCodeIsEnqueued() {
        if (postalUseCase.wasDownloadAlreadyConcluded()) {
            _downloadWasAlreadyConclude.call()
            hideProgress(progressModelDownload)
        }
    }

    fun downloadHasFailed() {
        postalUseCase.downloadHasFailed()
        hideProgress(progressModelDownload)
    }

    fun storePostalCode() {
        showProgress(progressModelRegister)

        if (postalUseCase.wasStoreAlreadyConcluded()) {
            _storeWasAlreadyConclude.call()
            hideProgress(progressModelRegister)
            return
        }

        _storePostalCodeWorkInfo.addSource(postalUseCase.storePostalCode()) {
            _storePostalCodeWorkInfo.value = it
        }
    }

    fun storePostalCodeIsEnqueued() {
        if (postalUseCase.wasStoreAlreadyConcluded()) {
            _storeWasAlreadyConclude.call()
            hideProgress(progressModelRegister)
        }
    }

    fun storePostalCodeHasFailed() {
        postalUseCase.storePostalCodeHasFailed()
        hideProgress(progressModelRegister)
    }
}