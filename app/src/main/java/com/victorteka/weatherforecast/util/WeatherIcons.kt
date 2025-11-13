package com.victorteka.weatherforecast.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

fun getWeatherIcon(weatherMain: String): ImageVector {
    return when (weatherMain.lowercase()) {
        "clear" -> Icons.Default.WbSunny
        "clouds" -> Icons.Default.Cloud
        "rain", "drizzle" -> Icons.Default.Opacity
        "thunderstorm" -> Icons.Default.FlashOn
        "snow" -> Icons.Default.AcUnit
        else -> Icons.Default.Cloud
    }
}

fun getWeatherIconColor(weatherMain: String): Color {
    return when (weatherMain.lowercase()) {
        "clear" -> Color(0xFFFDB813)
        "clouds" -> Color.Gray
        "rain", "drizzle" -> Color(0xFF2196F3)
        "thunderstorm" -> Color(0xFF9C27B0)
        "snow" -> Color(0xFF00BCD4)
        else -> Color.Gray
    }
}

fun getWindDirection(degrees: Int): String {
    return when (degrees) {
        in 0..22, in 338..360 -> "N"
        in 23..67 -> "NE"
        in 68..112 -> "E"
        in 113..157 -> "SE"
        in 158..202 -> "S"
        in 203..247 -> "SW"
        in 248..292 -> "W"
        in 293..337 -> "NW"
        else -> "N"
    }
}