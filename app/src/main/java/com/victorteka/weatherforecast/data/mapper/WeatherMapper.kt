package com.victorteka.weatherforecast.data.mapper

import com.victorteka.weatherforecast.data.local.entity.WeatherEntity
import com.victorteka.weatherforecast.data.remote.model.ForecastItem
import com.victorteka.weatherforecast.domain.model.WeatherForecast


fun ForecastItem.toEntity(
    cityName: String,
    country: String,
    latitude: Double,
    longitude: Double,
    fetchedAt: Long
): WeatherEntity {
    return WeatherEntity(
        id = "${latitude}_${longitude}_$dt",
        timestamp = dt,
        temperature = main.temp,
        feelsLike = main.feelsLike,
        tempMin = main.tempMin,
        tempMax = main.tempMax,
        pressure = main.pressure,
        humidity = main.humidity,
        weatherMain = weather.firstOrNull()?.main ?: "",
        weatherDescription = weather.firstOrNull()?.description ?: "",
        weatherIcon = weather.firstOrNull()?.icon ?: "",
        windSpeed = wind.speed,
        windDeg = wind.deg,
        dateTime = dtTxt,
        cityName = cityName,
        country = country,
        latitude = latitude,
        longitude = longitude,
        fetchedAt = fetchedAt
    )
}

fun WeatherEntity.toDomainModel(): WeatherForecast {
    return WeatherForecast(
        id = id,
        timestamp = timestamp,
        temperature = temperature,
        feelsLike = feelsLike,
        tempMin = tempMin,
        tempMax = tempMax,
        pressure = pressure,
        humidity = humidity,
        weatherMain = weatherMain,
        weatherDescription = weatherDescription,
        weatherIcon = weatherIcon,
        windSpeed = windSpeed,
        windDeg = windDeg,
        dateTime = dateTime,
        cityName = cityName,
        country = country
    )
}