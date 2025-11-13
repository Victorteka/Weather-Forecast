package com.victorteka.weatherforecast.di

import android.content.Context
import com.victorteka.weatherforecast.data.local.dao.WeatherDao
import com.victorteka.weatherforecast.data.remote.WeatherApiService
import com.victorteka.weatherforecast.data.repository.WeatherRepositoryImpl
import com.victorteka.weatherforecast.domain.repository.WeatherRepository
import com.victorteka.weatherforecast.util.LocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiService: WeatherApiService,
        weatherDao: WeatherDao,
        context: Context,
        locationManager: LocationManager
    ): WeatherRepository {
        return WeatherRepositoryImpl(apiService, weatherDao, locationManager, context)
    }
}