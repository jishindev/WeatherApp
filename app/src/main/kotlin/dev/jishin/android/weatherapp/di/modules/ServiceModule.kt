package dev.jishin.android.weatherapp.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.jishin.android.weatherapp.home.services.LocationService

@Module
abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract fun locationService() : LocationService
}