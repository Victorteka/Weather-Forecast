package com.victorteka.weatherforecast.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object MainGraph

@Serializable
object WeatherListDestination

@Serializable
data class WeatherDetailsDestination(
    val forecastId: String
)