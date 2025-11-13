package com.victorteka.weatherforecast.domain.repository

import com.victorteka.weatherforecast.domain.model.WeatherForecast
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeatherForecast(): Flow<Result<List<WeatherForecast>>>
    fun getForecastById(id: String): Flow<Result<WeatherForecast>>
}