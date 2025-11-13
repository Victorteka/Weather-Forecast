package com.victorteka.weatherforecast.domain.model

data class WeatherForecast(
    val id: String,
    val timestamp: Long,
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    val weatherMain: String,
    val weatherDescription: String,
    val weatherIcon: String,
    val windSpeed: Double,
    val windDeg: Int,
    val dateTime: String,
    val cityName: String,
    val country: String
)

data class Location(
    val latitude: Double,
    val longitude: Double
)