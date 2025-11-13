package com.victorteka.weatherforecast.presentation.weatherdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.victorteka.weatherforecast.domain.model.WeatherForecast
import com.victorteka.weatherforecast.domain.repository.WeatherRepository
import com.victorteka.weatherforecast.presentation.navigation.WeatherDetailsDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WeatherRepository
) : ViewModel() {

    private val destination = savedStateHandle.toRoute<WeatherDetailsDestination>()
    private val forecastId = destination.forecastId

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadForecast()
    }

    private fun loadForecast() {
        _uiState.update { state ->
            state.copy(isLoading = true)
        }
        viewModelScope.launch {
            repository.getForecastById(forecastId)
                .catch { e ->
                    _uiState.update { state ->
                        state.copy(isLoading = false, errorMsg = e.message ?: "Failed to load forecast")
                    }
                }
                .collect { result ->
                    result.onSuccess { forecast ->
                        _uiState.update { state ->
                            state.copy(isLoading = false, forecast = forecast)
                        }
                    }.onFailure { e ->
                        _uiState.update { state ->
                            state.copy(isLoading = false, errorMsg = e.message ?: "Failed to load forecast")
                        }
                    }
                }
        }
    }
}

data class DetailUiState (
    val isLoading : Boolean = false,
    val forecast: WeatherForecast? = null,
    val errorMsg: String? = null
)