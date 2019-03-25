package dev.jishin.android.weatherapp.network.models

import com.google.gson.annotations.SerializedName


data class WeatherResponse(
    @SerializedName("location") val location: LocationData,
    @SerializedName("current") val current: CurrentWeather,
    @SerializedName("forecast") val forecast: Forecast
)

data class LocationData(
     @SerializedName("name") val name: String?
)

data class CurrentWeather(
     @SerializedName("temp_c") val tempC: Float?
)

data class Forecast(
     @SerializedName("forecastday") val forecastDay: List<ForecastDay>?
)

data class ForecastDay(
     @SerializedName("date") val date: String?,
     @SerializedName("day") val day: Day?
)

data class Day(
     @SerializedName("avgtemp_c") val averageTempC: Float?
)