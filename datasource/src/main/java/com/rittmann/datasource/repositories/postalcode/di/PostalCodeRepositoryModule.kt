package com.rittmann.datasource.repositories.postalcode.di

import com.rittmann.datasource.local.dao.PostalCodeDao
import com.rittmann.datasource.repositories.postalcode.PostalCodeRepository
import com.rittmann.datasource.repositories.postalcode.PostalCodeRepositoryImpl
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