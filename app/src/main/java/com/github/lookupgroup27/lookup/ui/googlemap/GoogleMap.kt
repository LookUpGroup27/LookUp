package com.github.lookupgroup27.lookup.ui.googlemap

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

@Composable
fun GoogleMapScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current
  var hasLocationPermission by remember { mutableStateOf(false) }
  val locationProvider = LocationProviderSingleton.getInstance(context)
  var autoCenteringEnabled by remember { mutableStateOf(true) } // New state for auto-centering

  LaunchedEffect(Unit) {
    hasLocationPermission =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    if (hasLocationPermission) {
      locationProvider.requestLocationUpdates()
    } else {
      // Request permission
      ActivityCompat.requestPermissions(
          context as Activity,
          arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
          LOCATION_PERMISSION_REQUEST_CODE)
      Toast.makeText(
              context, "Location permission is required to access the map.", Toast.LENGTH_LONG)
          .show()
    }
  }

  Scaffold(
      modifier = Modifier.testTag("googleMapScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      content = { padding ->
        Column {
          // Add buttons to toggle map modes
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .background(Color(0xFF0D1023)) // Set your desired color here
                      .padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                      Button(onClick = { autoCenteringEnabled = true }) {
                        Text(text = "Auto Center On")
                      }
                      Button(onClick = { autoCenteringEnabled = false }) {
                        Text(text = "Auto Center Off")
                      }
                    }
              }

          // Map view below the buttons
          MapView(
              padding,
              hasLocationPermission,
              locationProvider.currentLocation.value,
              autoCenteringEnabled // Pass the state
              )
        }
      })
}

@Composable
fun MapView(
    padding: PaddingValues,
    hasLocationPermission: Boolean,
    location: Location?,
    autoCenteringEnabled: Boolean
) {
  var mapProperties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }
  var mapUiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }
  val cameraPositionState = rememberCameraPositionState()

  // Update the camera position whenever location changes
  LaunchedEffect(location, autoCenteringEnabled) {
    if (hasLocationPermission && location != null && autoCenteringEnabled) {
      val latLng = LatLng(location.latitude, location.longitude)
      val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5f)
      cameraPositionState.animate(cameraUpdate)
    }
  }

  GoogleMap(
      modifier = Modifier.fillMaxSize().padding(padding),
      properties = mapProperties,
      uiSettings = mapUiSettings,
      cameraPositionState = cameraPositionState) {
        if (hasLocationPermission && location != null) {
          val latLng = LatLng(location.latitude, location.longitude)
          Marker(state = MarkerState(position = latLng), title = "You are here")
        } else {
          //  case where location is not available
        }
      }
}
