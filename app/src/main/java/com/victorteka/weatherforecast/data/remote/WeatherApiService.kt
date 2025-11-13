package com.victorteka.weatherforecast.data.remote

import com.victorteka.weatherforecast.BuildConfig
import com.victorteka.weatherforecast.data.remote.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = BuildConfig.WEATHER_API_KEY,
        @Query("units") units: String = "metric",
    ): WeatherResponse
}