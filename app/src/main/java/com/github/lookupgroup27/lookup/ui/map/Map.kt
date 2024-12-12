package com.github.lookupgroup27.lookup.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.map.MapSurfaceView
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.google.firebase.auth.FirebaseAuth

private const val LOCATION_PERMISSION_REQUEST_CODE: Int = 1001

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun MapScreen(navigationActions: NavigationActions, mapViewModel: MapViewModel) {

  val context = LocalContext.current
  val user = FirebaseAuth.getInstance().currentUser
  val isUserLoggedIn = user != null
  val activity =
      context as? ComponentActivity ?: null.also { Log.e("MapScreen", "MainActivity not found") }

  var hasLocationPermission by remember { mutableStateOf(false) }
  val locationProvider = LocationProviderSingleton.getInstance(context)

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
  if (locationProvider.currentLocation.value != null) {
    DisposableEffect(Unit) {
      val originalOrientation =
          activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
      activity?.let {
        mapViewModel.lockScreenOrientation(it)
        mapViewModel.registerSensorListener(it)
      }

      onDispose {
        activity?.let {
          mapViewModel.unregisterSensorListener(it)
          mapViewModel.unlockScreenOrientation(it, originalOrientation)
        }
      }
    }
  }

  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            isUserLoggedIn = isUserLoggedIn,
            selectedItem = Route.MAP)
      }) { innerPadding ->
        if (locationProvider.currentLocation.value != null) {
          Box(modifier = Modifier.fillMaxSize().padding(innerPadding).testTag("map_screen")) {
            AndroidView(
                factory = { context -> MapSurfaceView(context, mapViewModel.mapRenderer) },
                modifier = Modifier.fillMaxSize().testTag("glSurfaceView"))
          }
        }
      }
}
