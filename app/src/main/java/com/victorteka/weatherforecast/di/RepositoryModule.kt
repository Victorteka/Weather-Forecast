package com.victorteka.weatherforecast.di

import com.victorteka.weatherforecast.data.local.dao.WeatherDao
import com.victorteka.weatherforecast.data.remote.WeatherApiService
import com.victorteka.weatherforecast.data.repository.WeatherRepositoryImpl
import com.victorteka.weatherforecast.domain.repository.WeatherRepository
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
        apiKey: String
    ): WeatherRepository {
        return WeatherRepositoryImpl(apiService, weatherDao, apiKey)
    }
}