package dev.jishin.android.weatherapp.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dev.jishin.android.weatherapp.custom.DaggerViewModelFactory
import dev.jishin.android.weatherapp.custom.DaggerVmFactory

@Module
abstract class VmFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(daggerVmFactory: DaggerViewModelFactory/*DaggerVmFactory*/): ViewModelProvider.Factory

}