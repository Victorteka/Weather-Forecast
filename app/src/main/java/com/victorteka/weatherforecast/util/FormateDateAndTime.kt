package com.victorteka.weatherforecast.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy 'at' HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}