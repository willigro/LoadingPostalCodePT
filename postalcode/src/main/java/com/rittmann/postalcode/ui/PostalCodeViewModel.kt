package com.rittmann.postalcode.ui

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import com.rittmann.androidtools.log.log
import com.rittmann.baselifecycle.livedata.SingleLiveEvent
import com.rittmann.common.lifecycle.BaseViewModelApp
import com.rittmann.common.usecase.PostalCodeUseCase
import javax.inject.Inject

class PostalCodeViewModel @Inject constructor(
    private val postalUseCase: PostalCodeUseCase
) : BaseViewModelApp() {

    private val _downloadWasAlreadyConclude: SingleLiveEvent<Void> = SingleLiveEvent()
    val downloadWasAlreadyConclude: LiveData<Void> get() = _downloadWasAlreadyConclude

    fun download(): LiveData<WorkInfo>? {
        if (postalUseCase.downloadWasAlreadyConcluded()) {
            _downloadWasAlreadyConclude.call()
            return null
        }
        return postalUseCase.download()
    }

    fun downloadHasFailed() {
        postalUseCase.downloadHasFailed()
    }

    fun downloadWasConcluded() {
        "downloadWasConcluded".log()
        // Mark as concluded
        postalUseCase.downloadConcluded()

        // Notify that was concluded
        _downloadWasAlreadyConclude.call()
    }

    fun storePostalCode(): LiveData<WorkInfo>?  {
        "storePostalCode".log()
        return postalUseCase.storePostalCode()
    }
}