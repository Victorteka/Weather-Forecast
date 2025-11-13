package com.victorteka.weatherforecast.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorteka.weatherforecast.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun loadWeatherForCurrentLocation() {
        _uiState.update { state ->
            state.copy(isLoading = true)
        }
        viewModelScope.launch {
            repository.getWeatherForecast().catch { emit(Result.failure(it)) }
                .collect { result ->
                    result.onSuccess { forecasts ->
                        _uiState.update { state ->
                            state.copy(isLoading = false, forecasts = forecasts)
                        }
                    }.onFailure { exception ->
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                errorMsg = exception.message ?: "Something went wrong"
                            )
                        }
                    }
                }
        }
    }
}