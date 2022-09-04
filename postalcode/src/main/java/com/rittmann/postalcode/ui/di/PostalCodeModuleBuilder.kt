package com.rittmann.postalcode.ui.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.rittmann.common.ActivityScoped
import com.rittmann.common.ViewModelKey
import com.rittmann.common.datasource.local.PostalCodeDao
import com.rittmann.common.datasource.sharedpreferences.SharedPreferencesModel
import com.rittmann.common.repositories.PostalCodeRepository
import com.rittmann.common.repositories.PostalCodeRepositoryImpl
import com.rittmann.common.usecase.PostalCodeUseCase
import com.rittmann.common.usecase.PostalCodeUseCaseImpl
import com.rittmann.common.viewmodel.ViewModelFactory
import com.rittmann.postalcode.ui.PostalCodeFragment
import com.rittmann.postalcode.ui.PostalCodeViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap


@Module
abstract class PostalCodeModuleBuilder {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [PostalCodeModuleDependencies::class])
    abstract fun bindPostalCodeFragment(): PostalCodeFragment
}

@Module(includes = [PostalCodeUseCaseModule::class])
abstract class PostalCodeModuleDependencies {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @IntoMap
    @Binds
    @ViewModelKey(PostalCodeViewModel::class)
    abstract fun bindPostalCodeViewModel(pokeListViewModel: PostalCodeViewModel): ViewModel
}

@Module(includes = [PostalCodeRepositoryModule::class])
class PostalCodeUseCaseModule {

    @Provides
    fun providePostalCodeUseCase(
        sharedPreferencesModel: SharedPreferencesModel,
        workManager: WorkManager,
        repository: PostalCodeRepository,
    ): PostalCodeUseCase = PostalCodeUseCaseImpl(
        sharedPreferencesModel,
        workManager,
        repository,
    )
}

@Module(includes = [])
class PostalCodeRepositoryModule {

    @Provides
    fun provideCryptoRepository(postalCodeDao: PostalCodeDao): PostalCodeRepository =
        PostalCodeRepositoryImpl(postalCodeDao)
}