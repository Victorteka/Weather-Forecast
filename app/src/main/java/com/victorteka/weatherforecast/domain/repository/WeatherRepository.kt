package com.victorteka.weatherforecast.domain.repository

import com.victorteka.weatherforecast.domain.model.Location
import com.victorteka.weatherforecast.domain.model.WeatherForecast
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeatherForecast(location: Location): Flow<Result<List<WeatherForecast>>>
    suspend fun refreshWeatherForecast(location: Location): Result<List<WeatherForecast>>
}