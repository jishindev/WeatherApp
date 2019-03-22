package dev.jishin.android.weatherapp.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.jishin.android.weatherapp.custom.DaggerVmFactory
import dev.jishin.android.weatherapp.di.ViewModelKey
import dev.jishin.android.weatherapp.home.MainVM

@Module
abstract class VmModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainVM::class)
    abstract fun bindMainVM(mainVM: MainVM): ViewModel
}