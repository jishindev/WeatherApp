package dev.jishin.android.weatherapp.home

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.jishin.android.weatherapp.network.WeatherRepo
import dev.jishin.android.weatherapp.network.models.WeatherResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.gildor.coroutines.retrofit.Result
import timber.log.Timber
import javax.inject.Inject

class MainVM @Inject constructor(
    private val weatherRepo: WeatherRepo,
    private val geoCoder: Geocoder
) : ViewModel() {

    private lateinit var forecastLiveData: MutableLiveData<Result<WeatherResponse>>
    private val handler = CoroutineExceptionHandler { _, t ->
        Timber.e("Co-routine exception , _:, t: $t")
    }

    fun getForecast(
        cScope: CoroutineScope,
        location: Location,
        reloadRequired: Boolean = false
    ): MutableLiveData<Result<WeatherResponse>> {
        Timber.i("getForecast() location: $location, reloadRequired: $reloadRequired")

        if (!::forecastLiveData.isInitialized || reloadRequired) {
            forecastLiveData = MutableLiveData()

            cScope.launch(handler) {
                val cityName = getUserCity(this, location)
                Timber.d("getForecast(): cityName: $cityName")
                forecastLiveData.postValue(weatherRepo.getForecast(cityName))
            }
        }
        return forecastLiveData
    }

    private suspend fun getUserCity(
        cScope: CoroutineScope,
        location: Location
    ) = cScope.async {
        Timber.i("getUserCity() called")

        val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
        Timber.d("getUserCity(): addresses: $addresses")
        var city:String? = ""
        if (addresses.isNotEmpty()) {
            city = addresses[0].locality
        }

        Timber.d("getUserCity(): city: $city")
        return@async city
    }.await()
}
