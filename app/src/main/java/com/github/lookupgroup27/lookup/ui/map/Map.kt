package com.github.lookupgroup27.lookup.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.github.lookupgroup27.lookup.opengl.MyGLRenderer
import com.github.lookupgroup27.lookup.opengl.MyGLSurfaceView
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route

@Composable
fun MapScreen(navigationActions: NavigationActions) {
  // Retain GLRenderer across recompositions using a ViewModel or state holder
  val glRenderer = remember { MyGLRenderer() }
  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.MAP)
      }) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).testTag("map_screen")) {
          AndroidView(
              factory = { context -> MyGLSurfaceView(context, glRenderer) },
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
