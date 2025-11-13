package com.victorteka.weatherforecast.presentation.weather

import com.victorteka.weatherforecast.domain.model.Location
import com.victorteka.weatherforecast.domain.model.WeatherForecast

data class WeatherUiState (
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val forecasts: List<WeatherForecast> = emptyList(),
    val location: Location? = null,
    val errorMsg: String? = null
)