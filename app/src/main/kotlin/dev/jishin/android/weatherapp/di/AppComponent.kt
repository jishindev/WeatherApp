package dev.jishin.android.weatherapp.di

import dagger.Component
import dagger.android.AndroidInjectionModule
import dev.jishin.android.weatherapp.App
import dev.jishin.android.weatherapp.di.modules.ActivityModule
import dev.jishin.android.weatherapp.di.modules.AppModule
import dev.jishin.android.weatherapp.di.modules.ServiceModule
import dev.jishin.android.weatherapp.di.modules.VmFactoryModule
import dev.jishin.android.weatherapp.home.services.LocationService
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        VmFactoryModule::class,
        AppModule::class,
        ActivityModule::class,
        ServiceModule::class]
)
interface AppComponent {
    fun inject(app: App)
    fun inject(locationService: LocationService)
}