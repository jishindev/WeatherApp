package dev.jishin.android.weatherapp.network.models

import com.squareup.moshi.Json

data class WeatherResponse(
    @Json(name = "location") val location: LocationData,
    @Json(name = "current") val current: CurrentWeather,
    @Json(name = "forecast") val forecast: List<Forecast>
) {
    data class LocationData(
        @Json(name = "name") val name: String
    )

    data class CurrentWeather(
        @Json(name = "temp_c") val tempC: Float
    )

    data class Forecast(
        @Json(name = "date") val date: String,
        @Json(name = "day") val day: Day
    ) {
        data class Day(
            @Json(name = "avgtemp_c") val averageTempC: Float
        )
    }
}