package com.victorteka.weatherforecast.data.repository

import android.content.Context
import com.victorteka.weatherforecast.data.local.dao.WeatherDao
import com.victorteka.weatherforecast.data.mapper.toDomainModel
import com.victorteka.weatherforecast.data.mapper.toEntity
import com.victorteka.weatherforecast.data.remote.WeatherApiService
import com.victorteka.weatherforecast.domain.model.Location
import com.victorteka.weatherforecast.domain.model.WeatherForecast
import com.victorteka.weatherforecast.domain.repository.WeatherRepository
import com.victorteka.weatherforecast.util.LocationManager
import com.victorteka.weatherforecast.util.isNetworkAvailable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao,
    private val locationManager: LocationManager,
    private val context: Context
) : WeatherRepository {

    override fun getWeatherForecast(): Flow<Result<List<WeatherForecast>>> = flow {
        val location = locationManager.getCurrentLocation().getOrElse {
            // fallback: Tokyo
            Location(35.6764, 139.6500)
        }

        val localData = weatherDao.getWeatherForecast(
            location.latitude, location.longitude
        ).firstOrNull()

        if (!localData.isNullOrEmpty()) {
            emit(Result.success(localData.map { it.toDomainModel() }))
        }

        if (!isNetworkAvailable(context)) {
            // No internet → just return cached data
            if (!localData.isNullOrEmpty()) {
                emit(Result.success(localData.map { it.toDomainModel() }))
            }
            return@flow
        }

        try {
            val response = apiService.getWeatherForecast(
                latitude = location.latitude,
                longitude = location.longitude,
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

            weatherDao.deleteWeatherForLocation(location.latitude, location.longitude)
            weatherDao.insertWeatherForecast(entities)

            val oldTimestamp = currentTime - TimeUnit.DAYS.toMillis(7)
            weatherDao.deleteOldForecasts(oldTimestamp)

            emit(Result.success(entities.map { it.toDomainModel() }))
        } catch (e: Exception) {
            if (!localData.isNullOrEmpty()) {
                emit(Result.success(localData.map { it.toDomainModel() }))
            } else {
                emit(Result.failure(e))
            }
        }
    }

    override fun getForecastById(id: String): Flow<Result<WeatherForecast>> {
        return weatherDao.getForecastById(id)
            .map { entity ->
                if (entity != null) {
                    Result.success(entity.toDomainModel())
                } else {
                    Result.failure(Exception("Forecast not found"))
                }
            }
    }
}