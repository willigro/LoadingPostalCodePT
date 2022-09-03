package com.rittmann.wtest

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
abstract class MainModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = [MainModuleDependencies::class])
    abstract fun bindMainActivity(): MainActivity
}

@Module
abstract class MainModuleDependencies {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @IntoMap
    @Binds
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel
}
