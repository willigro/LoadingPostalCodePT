package com.rittmann.common.repositories.postecode.di

import com.rittmann.common.datasource.local.PostalCodeDao
import com.rittmann.common.repositories.postecode.PostalCodeRepository
import com.rittmann.common.repositories.postecode.PostalCodeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class PostalCodeRepositoryModule {

    @Provides
    fun provideCryptoRepository(postalCodeDao: PostalCodeDao): PostalCodeRepository =
        PostalCodeRepositoryImpl(postalCodeDao)
}