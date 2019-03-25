package dev.jishin.android.weatherapp.di.modules

import android.content.Context
import android.location.Geocoder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dev.jishin.android.weatherapp.network.RetrofitFactory
import dev.jishin.android.weatherapp.network.WeatherApi
import dev.jishin.android.weatherapp.network.WeatherRepo
import java.util.*
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideContext() = context

    @Singleton
    @Provides
    fun provideWeatherApi() = RetrofitFactory.getApi<WeatherApi>()

    @Singleton
    @Provides
    fun provideWeatherRepo(weatherApi: WeatherApi) = WeatherRepo(weatherApi)

    @Singleton
    @Provides
    fun provideFusedLocationProvider(context: Context) = LocationServices.getFusedLocationProviderClient(context)!!

    @Singleton
    @Provides
    fun provideLocalBroadcastManager(context: Context) = LocalBroadcastManager.getInstance(context)

    @Singleton
    @Provides
    fun provideGeoCoder(context: Context) = Geocoder(context, Locale.getDefault())
}