package com.rittmann.postalcode.di

import com.rittmann.postalcode.ui.list.PostalCodeModuleBuilder
import dagger.Module

@Module(includes = [PostalCodeModuleBuilder::class])
abstract class PostalCodeModule