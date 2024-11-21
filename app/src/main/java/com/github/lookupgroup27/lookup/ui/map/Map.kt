package com.github.lookupgroup27.lookup.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.github.lookupgroup27.lookup.MainActivity
import com.github.lookupgroup27.lookup.model.map.MapSurfaceView
import com.github.lookupgroup27.lookup.model.map.Renderer
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun MapScreen(navigationActions: NavigationActions) {
  val glRenderer = remember { Renderer() }
  val context = LocalContext.current
  val activity = context as MainActivity

  DisposableEffect(Unit) {
    // Lock the screen orientation to portrait mode
    val originalOrientation = activity.requestedOrientation
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    // Register the rotation sensor to control the camera orientation
    val sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    sensorManager.registerListener(glRenderer.camera, orientation, SensorManager.SENSOR_DELAY_NORMAL)

    onDispose {
      sensorManager.unregisterListener(glRenderer.camera)
      activity.requestedOrientation = originalOrientation
    }
  }

  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.MAP)
      }) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).testTag("map_screen")) {
          AndroidView(
              factory = { context -> MapSurfaceView(context, glRenderer) },
              modifier = Modifier.fillMaxSize().testTag("glSurfaceView"))
          MovingBox(Alignment.CenterStart, "left") { glRenderer.camera.turnLeft() }
          MovingBox(Alignment.CenterEnd, "right") { glRenderer.camera.turnRight() }
          MovingBox(Alignment.TopCenter, "up") { glRenderer.camera.turnUp() }
          MovingBox(Alignment.BottomCenter, "down") { glRenderer.camera.turnDown() }
          MovingBox(Alignment.TopStart, "tilt left") { glRenderer.camera.tiltLeft() }
          MovingBox(Alignment.TopEnd, "tilt right") { glRenderer.camera.tiltRight() }
        }
      }
}

@Composable
fun MovingBox(alignment: Alignment, text: String, onClick: () -> Unit) {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = alignment) {
    Button(onClick = onClick) { Text(text) }
  }
}

@Preview
@Composable
fun MapScreenPreview() {
  MapScreen(NavigationActions(NavHostController(LocalContext.current)))
}
