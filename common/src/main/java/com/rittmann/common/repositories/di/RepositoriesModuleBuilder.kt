package com.rittmann.common.repositories.di

import com.rittmann.common.repositories.postecode.di.PostalCodeRepositoryModule
import dagger.Module

@Module(includes = [PostalCodeRepositoryModule::class])
abstract class RepositoriesModuleBuilder