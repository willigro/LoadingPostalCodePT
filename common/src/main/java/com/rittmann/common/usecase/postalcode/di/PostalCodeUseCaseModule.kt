package com.rittmann.common.usecase.postalcode.di

import androidx.work.WorkManager
import com.rittmann.common.datasource.sharedpreferences.SharedPreferencesModel
import com.rittmann.common.repositories.postecode.PostalCodeRepository
import com.rittmann.common.repositories.postecode.di.PostalCodeRepositoryModule
import com.rittmann.common.usecase.postalcode.PostalCodeUseCase
import com.rittmann.common.usecase.postalcode.PostalCodeUseCaseImpl
import dagger.Module
import dagger.Provides

@Module(includes = [PostalCodeRepositoryModule::class])
class PostalCodeUseCaseModule {

    @Provides
    fun providePostalCodeUseCase(
        sharedPreferencesModel: SharedPreferencesModel,
        workManager: WorkManager,
        repository: PostalCodeRepository,
    ): PostalCodeUseCase = PostalCodeUseCaseImpl(
        sharedPreferencesModel,
        workManager,
        repository,
    )
}
