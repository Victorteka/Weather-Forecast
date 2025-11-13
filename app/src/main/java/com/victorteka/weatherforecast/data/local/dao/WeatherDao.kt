package com.victorteka.weatherforecast.data.local.dao

import androidx.room.*
import com.victorteka.weatherforecast.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather_forecast WHERE latitude = :lat AND longitude = :lon ORDER BY timestamp ASC")
    fun getWeatherForecast(lat: Double, lon: Double): Flow<List<WeatherEntity>>

    @Query("SELECT * FROM weather_forecast WHERE latitude = :lat AND longitude = :lon ORDER BY timestamp ASC")
    suspend fun getWeatherForecastOnce(lat: Double, lon: Double): List<WeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherForecast(forecasts: List<WeatherEntity>)

    @Query("DELETE FROM weather_forecast WHERE latitude = :lat AND longitude = :lon")
    suspend fun deleteWeatherForLocation(lat: Double, lon: Double)

    @Query("DELETE FROM weather_forecast WHERE fetchedAt < :timestamp")
    suspend fun deleteOldForecasts(timestamp: Long)
}