package dev.jishin.android.weatherapp

import android.app.Activity
import android.app.Application
import android.app.Service
import dagger.android.*
import dev.jishin.android.weatherapp.di.DaggerAppComponent
import dev.jishin.android.weatherapp.di.modules.AppModule
import dev.jishin.android.weatherapp.utils.registerActivityCreatedCallbacks
import timber.log.Timber
import javax.inject.Inject


class App : Application(), HasActivityInjector, HasServiceInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber Logging
        Timber.plant(Timber.DebugTree())

        // Initialize Dagger app component
        initDagger()
    }

    private fun initDagger() {

        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
            .inject(this)

        registerActivityCreatedCallbacks { activity ->
            if (activity is HasFragmentInjector) {
                AndroidInjection.inject(activity)
            }
        }
    }

    override fun activityInjector() = dispatchingAndroidInjector
    override fun serviceInjector() = dispatchingServiceInjector
}