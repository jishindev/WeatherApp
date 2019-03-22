package dev.jishin.android.weatherapp.home

import android.Manifest
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import dev.jishin.android.weatherapp.network.WeatherRepo
import dev.jishin.android.weatherapp.network.models.WeatherResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.gildor.coroutines.retrofit.Result
import timber.log.Timber
import javax.inject.Inject

class MainVM @Inject constructor(
    private val weatherRepo: WeatherRepo
) : ViewModel() {

    private lateinit var forecastLiveData: MutableLiveData<Result<WeatherResponse>>
    private lateinit var userCityLiveData: MutableLiveData<String?>

    fun getForecast(cityName: String?, reloadRequired: Boolean = false): MutableLiveData<Result<WeatherResponse>> {

        if (!::forecastLiveData.isInitialized || reloadRequired) {
            forecastLiveData = MutableLiveData()

            GlobalScope.launch {
                forecastLiveData.postValue(weatherRepo.getForecast(cityName))
            }
        }

        return forecastLiveData
    }


    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun getUserCity(location: Location): MutableLiveData<String?> {

        if (!::userCityLiveData.isInitialized) {
            Timber.i("getUserCity: initializing userCityLiveData")
            userCityLiveData = MutableLiveData()
        }
        return userCityLiveData
    }
}