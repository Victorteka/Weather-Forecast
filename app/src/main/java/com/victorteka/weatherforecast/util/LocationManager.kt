package com.victorteka.weatherforecast.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.victorteka.weatherforecast.domain.model.Location
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    private val context: Context
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Result<Location> {
        return try {
            val permission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                return Result.failure(SecurityException("Location permission denied"))
            }

            val location = fusedLocationClient.lastLocation.await()
                ?: return Result.failure(IllegalStateException("Location unavailable"))

            Result.success(Location(location.latitude, location.longitude))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}