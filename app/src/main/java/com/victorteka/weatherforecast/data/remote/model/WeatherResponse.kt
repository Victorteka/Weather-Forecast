package com.victorteka.weatherforecast.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("list") val list: List<ForecastItem>,
    @SerialName("city") val city: City
)

@Serializable
data class ForecastItem(
    @SerialName("dt") val dt: Long,
    @SerialName("main") val main: Main,
    @SerialName("weather") val weather: List<Weather>,
    @SerialName("wind") val wind: Wind,
    @SerialName("dt_txt") val dtTxt: String
)

@Serializable
data class Main(
    @SerialName("temp") val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    @SerialName("temp_min") val tempMin: Double,
    @SerialName("temp_max") val tempMax: Double,
    @SerialName("pressure") val pressure: Int,
    @SerialName("humidity") val humidity: Int
)

@Serializable
data class Weather(
    @SerialName("id") val id: Int,
    @SerialName("main") val main: String,
    @SerialName("description") val description: String,
    @SerialName("icon") val icon: String
)

@Serializable
data class Wind(
    @SerialName("speed") val speed: Double,
    @SerialName("deg") val deg: Int
)

@Serializable
data class City(
    @SerialName("name") val name: String,
    @SerialName("country") val country: String
)
