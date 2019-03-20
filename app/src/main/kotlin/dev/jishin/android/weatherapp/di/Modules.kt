package dev.jishin.android.weatherapp.di

import dev.jishin.android.weatherapp.home.MainVM
import dev.jishin.android.weatherapp.network.RetrofitFactory
import dev.jishin.android.weatherapp.network.WeatherApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single<WeatherApi> { RetrofitFactory.getApi() }
}

val vmModule = module {
    viewModel { MainVM(get()) }
}
