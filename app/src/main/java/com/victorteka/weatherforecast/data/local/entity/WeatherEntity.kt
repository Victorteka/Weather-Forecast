package com.victorteka.weatherforecast.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_forecast")
data class WeatherEntity(
    @PrimaryKey val id: String,
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
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val fetchedAt: Long
)