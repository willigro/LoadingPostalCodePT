package com.rittmann.common.usecase.postalcode.di

import android.content.Context
import com.rittmann.common.datasource.sharedpreferences.SharedPreferencesModel
import com.rittmann.common.repositories.postecode.PostalCodeRepository
import com.rittmann.common.repositories.postecode.di.PostalCodeRepositoryModule
import com.rittmann.common.usecase.postalcode.PostalCodeUseCase
import com.rittmann.common.usecase.postalcode.PostalCodeUseCaseImpl
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
