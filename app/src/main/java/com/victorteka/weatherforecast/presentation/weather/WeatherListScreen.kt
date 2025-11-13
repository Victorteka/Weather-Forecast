package com.victorteka.weatherforecast.presentation.weather

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.victorteka.weatherforecast.domain.model.WeatherForecast
import com.victorteka.weatherforecast.presentation.component.CurrentWeatherCard
import com.victorteka.weatherforecast.presentation.component.ErrorBanner
import com.victorteka.weatherforecast.presentation.component.ErrorView
import com.victorteka.weatherforecast.presentation.component.LoadingView
import com.victorteka.weatherforecast.presentation.component.PermissionRationaleCard
import com.victorteka.weatherforecast.presentation.component.WeatherForecastCard
import com.victorteka.weatherforecast.presentation.ui.theme.DeepBlue
import com.victorteka.weatherforecast.presentation.ui.theme.SkyBlue
import com.victorteka.weatherforecast.util.formatDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WeatherListRoute(
    onForecastClick: (WeatherForecast) -> Unit
) {
    val viewModel: WeatherViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    WeatherListScreen(
        uiState = uiState,
        context = context,
        onForecastClick = onForecastClick,
        loadWeatherForCurrentLocation = viewModel::loadWeatherForCurrentLocation,
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WeatherListScreen(
    uiState: WeatherUiState,
    context: Context,
    onForecastClick: (WeatherForecast) -> Unit,
    loadWeatherForCurrentLocation: () -> Unit,
) {
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        if (locationPermissionState.allPermissionsGranted) {
            loadWeatherForCurrentLocation()
        } else {
            locationPermissionState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(locationPermissionState.allPermissionsGranted) {
        if (locationPermissionState.allPermissionsGranted) {
            loadWeatherForCurrentLocation()
        } else if (locationPermissionState.permissions.any {
                !it.status.isGranted && !it.status.shouldShowRationale
            }) {
            // Permission permanently denied, use default location
            Log.d("TAG", "WeatherListScreen: ")
            loadWeatherForCurrentLocation()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Forecast") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepBlue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingView()
                }

                uiState.forecasts.isNotEmpty() -> {
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { loadWeatherForCurrentLocation() },
                        state = pullRefreshState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        WeatherList(
                            forecasts = uiState.forecasts,
                            onForecastClick = onForecastClick
                        )
                    }
                }

                uiState.errorMsg != null -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ErrorBanner(message = uiState.errorMsg)
                    }
                }
            }

            if (
                !locationPermissionState.allPermissionsGranted &&
                locationPermissionState.permissions.any { it.status.shouldShowRationale }
            ) {
                PermissionRationaleCard(
                    onRequestPermission = { locationPermissionState.launchMultiplePermissionRequest() }
                )
            }

        }
    }
}

@Composable
fun WeatherList(
    forecasts: List<WeatherForecast>,
    onForecastClick: (WeatherForecast) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SkyBlue.copy(alpha = 0.3f), Color.White)
                )
            ),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (forecasts.isNotEmpty()) {
            item {
                CurrentWeatherCard(forecast = forecasts.first())
            }
        }

        val groupedForecasts = forecasts.groupBy { forecast ->
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Date(forecast.timestamp * 1000))
        }

        groupedForecasts.forEach { (date, dayForecasts) ->
            item {
                Text(
                    text = formatDate(date),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(dayForecasts) { forecast ->
                WeatherForecastCard(
                    forecast = forecast,
                    onClick = { onForecastClick(forecast) }
                )
            }
        }
    }
}