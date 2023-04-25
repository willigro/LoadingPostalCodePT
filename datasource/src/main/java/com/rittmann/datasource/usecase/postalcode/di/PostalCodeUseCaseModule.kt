package com.rittmann.datasource.usecase.postalcode.di

import android.content.Context
import com.rittmann.datasource.usecase.postalcode.PostalCodeUseCase
import com.rittmann.datasource.usecase.postalcode.PostalCodeUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module(includes = [com.rittmann.datasource.repositories.postalcode.di.PostalCodeRepositoryModule::class])
@InstallIn(SingletonComponent::class)
class PostalCodeUseCaseModule {

    @Provides
    fun providePostalCodeUseCase(
        sharedPreferencesModel: com.rittmann.datasource.local.preferences.SharedPreferencesModel,
        @ApplicationContext context: Context,
        repository: com.rittmann.datasource.repositories.postalcode.PostalCodeRepository,
    ): PostalCodeUseCase = PostalCodeUseCaseImpl(
        sharedPreferencesModel,
        context,
        repository,
    )
}