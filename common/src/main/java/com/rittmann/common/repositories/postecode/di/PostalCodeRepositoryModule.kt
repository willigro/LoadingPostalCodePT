package com.rittmann.common.repositories.postecode.di

import com.rittmann.common.datasource.local.PostalCodeDao
import com.rittmann.common.repositories.postecode.PostalCodeRepository
import com.rittmann.common.repositories.postecode.PostalCodeRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class PostalCodeRepositoryModule {

    @Provides
    fun provideCryptoRepository(postalCodeDao: PostalCodeDao): PostalCodeRepository =
        PostalCodeRepositoryImpl(postalCodeDao)
}