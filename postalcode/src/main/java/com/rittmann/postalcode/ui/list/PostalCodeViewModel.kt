package com.rittmann.postalcode.ui.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.WorkInfo
import com.rittmann.baselifecycle.livedata.SingleLiveEvent
import com.rittmann.common.lifecycle.BaseViewModelApp
import com.rittmann.datasource.usecase.postalcode.PostalCodeUseCase
import com.rittmann.widgets.progress.ProgressPriorityControl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class PostalCodeViewModel @Inject constructor(
    private val postalUseCase: PostalCodeUseCase
) : BaseViewModelApp() {

    private var previous: LiveData<PagingData<com.rittmann.datasource.model.PostalCode>>? = null
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

    private val _postalCodes: MediatorLiveData<PagingData<com.rittmann.datasource.model.PostalCode>> = MediatorLiveData()
    val postalCodes: LiveData<PagingData<com.rittmann.datasource.model.PostalCode>> = _postalCodes

    fun loadPostalCodes(query: String) {
        viewModelScope.launch(Dispatchers.Main) {
            previous?.also {
                _postalCodes.removeSource(it)
            }
            previous = postalUseCase.pagingSource(query = query).cachedIn(viewModelScope)
            _postalCodes.addSource(previous!!) {
                _postalCodes.value = it
            }
        }
    }

    fun downloadPostalCodes() {
        showProgress(progressModelDownload)

        if (postalUseCase.wasDownloadAlreadyConcluded()) {
            _downloadWasAlreadyConclude.call()
            hideProgress(progressModelDownload)
            return
        }

        _downloadPostalCodeWorkInfo.addSource(postalUseCase.downloadPostalCodes()) {
            Log.i("TESTING", "UPDATING_LIVE_DATA $it")
            _downloadPostalCodeWorkInfo.value = it
        }
    }

    fun downloadPostalCodeIsEnqueued() {
        if (postalUseCase.wasDownloadAlreadyConcluded()) {
            _downloadWasAlreadyConclude.call()
            hideProgress(progressModelDownload)
        }
    }

    fun downloadPostalCodeIsRunning() {
        Log.i("TESTING", "downloadPostalCodeIsRunning progressModelDownload.added=${progressModelDownload.added}")
        if (postalUseCase.wasDownloadAlreadyConcluded().not()) {
            showProgress(progressModelDownload)
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