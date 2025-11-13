package com.victorteka.weatherforecast.data.repository

import com.victorteka.weatherforecast.data.local.dao.WeatherDao
import com.victorteka.weatherforecast.data.mapper.toDomainModel
import com.victorteka.weatherforecast.data.mapper.toEntity
import com.victorteka.weatherforecast.data.remote.WeatherApiService
import com.victorteka.weatherforecast.domain.model.Location
import com.victorteka.weatherforecast.domain.model.WeatherForecast
import com.victorteka.weatherforecast.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao,
    private val apiKey: String
) : WeatherRepository {

    override fun getWeatherForecast(location: Location): Flow<Result<List<WeatherForecast>>> {
        return weatherDao.getWeatherForecast(location.latitude, location.longitude)
            .map { entities ->
                if (entities.isNotEmpty()) {
                    Result.success(entities.map { it.toDomainModel() })
                } else {
                    Result.failure(Exception("No cached data available"))
                }
            }
    }

    override suspend fun refreshWeatherForecast(location: Location): Result<List<WeatherForecast>> {
        return try {
            val response = apiService.getWeatherForecast(
                latitude = location.latitude,
                longitude = location.longitude,
                apiKey = apiKey
            )

            val currentTime = System.currentTimeMillis()
            val entities = response.list.map { item ->
                item.toEntity(
                    cityName = response.city.name,
                    country = response.city.country,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    fetchedAt = currentTime
                )
            }

            // Clean old data and insert new
            weatherDao.deleteWeatherForLocation(location.latitude, location.longitude)
            weatherDao.insertWeatherForecast(entities)

            val oldTimestamp = currentTime - TimeUnit.DAYS.toMillis(7)
            weatherDao.deleteOldForecasts(oldTimestamp)

            Result.success(entities.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}