package com.rittmann.common.usecase.di

import com.rittmann.common.usecase.postalcode.di.PostalCodeUseCaseModule
import dagger.Module

@Module(includes = [PostalCodeUseCaseModule::class])
abstract class UseCasesModuleBuilder