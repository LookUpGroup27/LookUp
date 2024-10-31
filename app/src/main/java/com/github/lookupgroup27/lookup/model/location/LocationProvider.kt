package com.github.lookupgroup27.lookup.model.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class LocationProvider(private val context: Context) {
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
  var currentLocation = mutableStateOf<Location?>(null)

  fun requestLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED) {
      // Handle permission request
      return
    }
    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
      currentLocation.value = location
    }
  }
}
