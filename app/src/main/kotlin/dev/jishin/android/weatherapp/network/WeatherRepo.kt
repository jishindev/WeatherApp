package dev.jishin.android.weatherapp.network

import dev.jishin.android.weatherapp.network.models.WeatherResponse
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.awaitResult
import javax.inject.Inject

class WeatherRepo @Inject constructor(private val weatherApi: WeatherApi) {

    suspend fun getForecast(cityName: String?): Result<WeatherResponse> =
        weatherApi.getForecast(cityName).awaitResult()
}