package com.github.lookupgroup27.lookup.model.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

open class LocationProvider(
    private val context: Context,
    var currentLocation: MutableState<Location?> = mutableStateOf(null)
) {
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

  private val locationCallback =
      object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
          locationResult.lastLocation?.let { location -> currentLocation.value = location }
        }
      }

  fun requestLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED) {
      // Handle permission request
      return
    }

    val locationRequest =
        LocationRequest.create().apply {
          interval = 10000 // 10 seconds
          fastestInterval = 5000 // 5 seconds
          priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
  }
}
