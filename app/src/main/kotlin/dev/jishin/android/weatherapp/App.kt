package dev.jishin.android.weatherapp

import android.app.Application
import dev.jishin.android.weatherapp.di.networkModule
import dev.jishin.android.weatherapp.di.vmModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber Logging
        Timber.plant(Timber.DebugTree())

        // Initialize Koin DI
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(networkModule, vmModule)
        }
    }
}