package com.rittmann.postalcode.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.work.WorkInfo
import com.rittmann.androidtools.log.log
import com.rittmann.baselifecycle.livedata.SingleLiveEvent
import com.rittmann.common.lifecycle.BaseViewModelApp
import com.rittmann.common.usecase.postalcode.PostalCodeUseCase
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

    fun download() {
        if (postalUseCase.wasDownloadAlreadyConcluded()) {
            _downloadWasAlreadyConclude.call()
            return
        }

        _downloadPostalCodeWorkInfo.addSource(postalUseCase.download()) {
            _downloadPostalCodeWorkInfo.value = it
        }
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

    fun storePostalCode() {
        executeAsync {
            if (postalUseCase.wasStoreAlreadyConcluded().not()){
                executeMain {
                    _storePostalCodeWorkInfo.addSource(postalUseCase.storePostalCode()) {
                        _storePostalCodeWorkInfo.value = it
                    }
                }
            }
        }
    }

    fun storePostalCodeHasFailed() {
        postalUseCase.storePostalCodeHasFailed()
    }

    fun checkIfStoreWasConclude() {
        "checkIfStoreWasConclude".log()
        executeAsync {
            if (postalUseCase.wasStoreAlreadyConcluded()) {
                "store WAS AlreadyConcluded".log()
                executeMain {
                    _storeWasAlreadyConclude.call()
                }
            }
        }
    }
}