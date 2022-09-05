package com.rittmann.postalcode.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rittmann.common.ActivityScoped
import com.rittmann.common.ViewModelKey
import com.rittmann.common.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap


@Module
abstract class PostalCodeModuleBuilder {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [PostalCodeModuleDependencies::class])
    abstract fun bindPostalCodeFragment(): PostalCodeFragment
}

@Module()
abstract class PostalCodeModuleDependencies {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @IntoMap
    @Binds
    @ViewModelKey(PostalCodeViewModel::class)
    abstract fun bindPostalCodeViewModel(pokeListViewModel: PostalCodeViewModel): ViewModel
}