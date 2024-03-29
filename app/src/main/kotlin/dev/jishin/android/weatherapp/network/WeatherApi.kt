package dev.jishin.android.weatherapp.network

import dev.jishin.android.weatherapp.network.models.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast.json/")
    fun getForecast(
        @Query("q") cityName: String?,
        @Query("key") apiKey: String = "92d7c369ddc94b89b2762828191503",
        @Query("days") days: Int = 4
    ): Call<WeatherResponse>
}