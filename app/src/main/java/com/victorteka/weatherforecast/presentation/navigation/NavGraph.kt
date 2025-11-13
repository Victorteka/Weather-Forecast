package com.victorteka.weatherforecast.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.victorteka.weatherforecast.presentation.weather.WeatherListRoute
import com.victorteka.weatherforecast.presentation.weatherdetail.WeatherDetailRoute
import com.victorteka.weatherforecast.presentation.weatherdetail.WeatherDetailScreen

@Composable
fun WeatherNavGraph(
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = MainGraph
    ) {
        navigation<MainGraph>(startDestination = WeatherListDestination) {
            composable<WeatherListDestination> {
                WeatherListRoute(
                    onForecastClick = { forecast ->
                        navController.navigate(
                            WeatherDetailsDestination(forecastId = forecast.id)
                        )
                    }
                )
            }
            composable<WeatherDetailsDestination> { backStackEntry ->
                val destination = backStackEntry.toRoute<WeatherDetailsDestination>()
                WeatherDetailRoute(
                    forecastId = destination.forecastId,
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}