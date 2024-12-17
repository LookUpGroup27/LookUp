package com.github.lookupgroup27.lookup.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.map.MapSurfaceView
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.ui.theme.DarkPurple
import com.google.firebase.auth.FirebaseAuth
import components.BackgroundImage

private const val LOCATION_PERMISSION_REQUEST_CODE: Int = 1001

@OptIn(ExperimentalMaterial3Api::class)
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
  var refreshKey by remember { mutableStateOf(0) }

  LaunchedEffect(refreshKey) {
    hasLocationPermission =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    if (hasLocationPermission) {
      locationProvider.requestLocationUpdates()
    } else {
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
                factory = { context -> MapSurfaceView(context, mapViewModel) },
                modifier = Modifier.fillMaxSize().testTag("glSurfaceView"))
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally) {
                  Row {
                    Button(
                        content = { Text(stringResource(R.string.map_button_reset_text)) },
                        onClick = { mapViewModel.updateFov(MapViewModel.DEFAULT_FOV) },
                        modifier = Modifier.padding(16.dp))
                    Slider(
                        value = mapViewModel.zoomPercentage,
                        onValueChange = { mapViewModel.updateZoom(it) },
                        valueRange = 0f..100f,
                        steps = 100,
                        thumb = {
                          Box(
                              modifier =
                                  Modifier.size(30.dp)
                                      .background(
                                          MaterialTheme.colorScheme.primary, shape = CircleShape),
                              contentAlignment = Alignment.Center) {
                                Text(
                                    text = mapViewModel.zoomPercentage.toInt().toString(),
                                    color = Color.Unspecified,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold)
                              }
                        },
                        modifier =
                            Modifier.padding(16.dp)
                                .testTag(stringResource(R.string.map_slider_test_tag)))
                  }
                }
          }
        } else {
          Box(
              modifier = Modifier.fillMaxSize().padding(innerPadding).testTag("map_screen"),
              contentAlignment = Alignment.Center) {
                BackgroundImage(
                    painterResId = R.drawable.background_blurred,
                    contentDescription = stringResource(R.string.background_description),
                    testTag = "background_test_tag")

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                      Text(
                          text = "Unable to load map.",
                          fontSize = 18.sp,
                          color = Color.White,
                          fontWeight = FontWeight.Bold,
                          modifier =
                              Modifier.padding(bottom = 16.dp).testTag("map_screen_error_text"))
                      Button(
                          onClick = { refreshKey++ },
                          colors =
                              androidx.compose.material3.ButtonDefaults.buttonColors(
                                  containerColor = DarkPurple, contentColor = Color.White),
                          modifier = Modifier.testTag("refresh_button")) {
                            Text("Refresh")
                          }
                    }
              }
        }
      }
}
