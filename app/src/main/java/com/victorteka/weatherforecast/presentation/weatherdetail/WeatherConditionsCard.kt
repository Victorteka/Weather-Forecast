package com.victorteka.weatherforecast.presentation.weatherdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.ThermostatAuto
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.victorteka.weatherforecast.domain.model.WeatherForecast
import com.victorteka.weatherforecast.util.getWindDirection
import java.util.Locale

@Composable
fun TemperatureDetailsCard(forecast: WeatherForecast) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Temperature Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            DetailRow(
                icon = Icons.Default.ThermostatAuto,
                label = "Current",
                value = "${forecast.temperature.toInt()}°C",
                iconColor = Color(0xFFFF6B6B)
            )

            DetailRow(
                icon = Icons.Default.ArrowUpward,
                label = "Maximum",
                value = "${forecast.tempMax.toInt()}°C",
                iconColor = Color(0xFFFF6B6B)
            )

            DetailRow(
                icon = Icons.Default.ArrowDownward,
                label = "Minimum",
                value = "${forecast.tempMin.toInt()}°C",
                iconColor = Color(0xFF4ECDC4)
            )

            DetailRow(
                icon = Icons.Default.Thermostat,
                label = "Feels Like",
                value = "${forecast.feelsLike.toInt()}°C",
                iconColor = Color(0xFFFFA500)
            )
        }
    }
}

@Composable
fun WeatherConditionsCard(forecast: WeatherForecast) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Weather Conditions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            DetailRow(
                icon = Icons.Default.Cloud,
                label = "Condition",
                value = forecast.weatherMain,
                iconColor = Color(0xFF95A5A6)
            )

            DetailRow(
                icon = Icons.Default.Description,
                label = "Description",
                value = forecast.weatherDescription.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                },
                iconColor = Color(0xFF3498DB)
            )

            DetailRow(
                icon = Icons.Default.WaterDrop,
                label = "Humidity",
                value = "${forecast.humidity}%",
                iconColor = Color(0xFF3498DB)
            )

            DetailRow(
                icon = Icons.Default.Compress,
                label = "Pressure",
                value = "${forecast.pressure} hPa",
                iconColor = Color(0xFF9B59B6)
            )
        }
    }
}

@Composable
fun WindInformationCard(forecast: WeatherForecast) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Wind Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            DetailRow(
                icon = Icons.Default.Air,
                label = "Wind Speed",
                value = "${forecast.windSpeed} m/s",
                iconColor = Color(0xFF16A085)
            )

            DetailRow(
                icon = Icons.Default.Explore,
                label = "Wind Direction",
                value = "${forecast.windDeg}° ${getWindDirection(forecast.windDeg)}",
                iconColor = Color(0xFF2ECC71)
            )
        }
    }
}

@Composable
fun AdditionalInfoCard(forecast: WeatherForecast) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Additional Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            DetailRow(
                icon = Icons.Default.Schedule,
                label = "Forecast Time",
                value = forecast.dateTime,
                iconColor = Color(0xFF34495E)
            )

            DetailRow(
                icon = Icons.Default.LocationOn,
                label = "Location",
                value = "${forecast.cityName}, ${forecast.country}",
                iconColor = Color(0xFFE74C3C)
            )
        }
    }
}