package com.rittmann.datasource.usecase.postalcode.di

import android.content.Context
import com.rittmann.datasource.local.preferences.SharedPreferencesModel
import com.rittmann.datasource.repositories.postalcode.PostalCodeRepository
import com.rittmann.datasource.repositories.postalcode.di.PostalCodeRepositoryModule
import com.rittmann.datasource.usecase.postalcode.PostalCodeUseCase
import com.rittmann.datasource.usecase.postalcode.PostalCodeUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module(includes = [PostalCodeRepositoryModule::class])
@InstallIn(SingletonComponent::class)
class PostalCodeUseCaseModule {

    @Provides
    fun providePostalCodeUseCase(
        sharedPreferencesModel: SharedPreferencesModel,
        @ApplicationContext context: Context,
        repository: PostalCodeRepository,
    ): PostalCodeUseCase = PostalCodeUseCaseImpl(
        sharedPreferencesModel,
        context,
        repository,
    )
}