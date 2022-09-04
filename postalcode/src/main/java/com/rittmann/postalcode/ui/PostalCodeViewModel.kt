package com.rittmann.postalcode.ui

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.rittmann.androidtools.log.log
import com.rittmann.baselifecycle.livedata.SingleLiveEvent
import com.rittmann.common.lifecycle.BaseViewModelApp
import com.rittmann.common.mappers.lineStringFromCsvToPostalCodeList
import com.rittmann.common.model.PostalCode
import com.rittmann.common.usecase.PostalCodeUseCase
import com.rittmann.common.workmanager.POSTAL_CODE_FILE_PATH
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
        // Mark as concluded
        postalUseCase.downloadConcluded()

        // Notify that was concluded
        _downloadWasAlreadyConclude.call()
    }

    fun storePostalCode() {
        // TODO thread
        val postalCodes = arrayListOf<PostalCode>()
        csvReader().open(POSTAL_CODE_FILE_PATH) {
            var i = 0
            for (line in readAllAsSequence()) {
                if (i == 0) {
                    i++
                    continue
                }
                postalCodes.add(line.lineStringFromCsvToPostalCodeList())
            }
        }
        postalCodes.size.log()
    }
}