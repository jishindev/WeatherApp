package dev.jishin.android.weatherapp.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.jishin.android.weatherapp.home.MainActivity

@Module(includes = [VmModule::class])
abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}