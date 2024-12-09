package com.github.lookupgroup27.lookup.ui.map

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.map.Camera
import com.github.lookupgroup27.lookup.model.map.MapSurfaceView
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun MapScreen(navigationActions: NavigationActions, mapViewModel: MapViewModel = viewModel()) {
  val context = LocalContext.current
  val user = FirebaseAuth.getInstance().currentUser
  val isUserLoggedIn = user != null
  val activity =
      context as? ComponentActivity ?: null.also { Log.e("MapScreen", "MainActivity not found") }

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

  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            isUserLoggedIn = isUserLoggedIn,
            selectedItem = Route.MAP)
      }) { innerPadding ->
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
                      onClick = { mapViewModel.updateFov(Camera.DEFAULT_FOV) },
                      modifier = Modifier.padding(16.dp))
                  Slider(
                      value = mapViewModel.zoomPercentage,
                      onValueChange = { mapViewModel.updateZoom(it) },
                      valueRange = 0f..100f,
                      steps = 100,
                      modifier = Modifier.padding(16.dp))
                  //            TODO : Make test for the new composable in this new feature
                }
              }
        }
      }
}

@Preview
@Composable
fun MapScreenPreview() {
  MapScreen(NavigationActions(NavHostController(LocalContext.current)))
}
