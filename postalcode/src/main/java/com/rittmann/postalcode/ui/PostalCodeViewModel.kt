package com.rittmann.postalcode.ui

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import com.rittmann.common.lifecycle.BaseViewModelApp
import com.rittmann.common.usecase.PostalCodeUseCase
import javax.inject.Inject

class PostalCodeViewModel @Inject constructor(
    private val postalUseCase: PostalCodeUseCase
) : BaseViewModelApp() {

    fun download(): LiveData<WorkInfo> = postalUseCase.download()

    fun downloadHasFailed() {
        postalUseCase.downloadHasFailed()
    }
}