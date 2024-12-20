package com.github.lookupgroup27.lookup.ui.planetselection

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.lookupgroup27.lookup.model.planetselection.PlanetSurfaceView
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.planetselection.components.PlanetSelectionRow

/**
 * Composable function for the Planet Selection screen.
 *
 * This screen allows the user to select a planet to view in 3D.
 *
 * @param viewModel The ViewModel for the Planet Selection screen.
 * @param navigationActions The navigation actions to handle screen transitions.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlanetSelectionScreen(
    viewModel: PlanetSelectionViewModel = viewModel(),
    navigationActions: NavigationActions
) {
  val context = LocalContext.current

  // Lock the screen orientation to portrait mode.
  DisposableEffect(Unit) {
    val activity = context as? ComponentActivity
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
  }

  val planets = viewModel.planets
  val selectedPlanet by viewModel.selectedPlanet.collectAsState()

  // Reference to the PlanetSurfaceView to update it
  var planetSurfaceView by remember { mutableStateOf<PlanetSurfaceView?>(null) }

  Scaffold(
      topBar = {
        Column(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {
          IconButton(
              onClick = { navigationActions.navigateTo(Screen.MENU) },
              modifier = Modifier.padding(16.dp).testTag("go_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White)
              }

          PlanetSelectionRow(planets = planets, onPlanetSelected = { viewModel.selectPlanet(it) })

          Spacer(modifier = Modifier.height(50.dp))

          Text(
              text = selectedPlanet.name,
              color = White,
              fontSize = 50.sp,
              fontWeight = FontWeight.Light,
              modifier =
                  Modifier.padding(20.dp)
                      .align(Alignment.CenterHorizontally)
                      .testTag("planet_name"))
        }
      },
      content = {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally) {

              // Bottom: Planet renderer
              Box(
                  modifier = Modifier.weight(1f).fillMaxWidth().background(Color.Transparent),
                  contentAlignment = Alignment.Center) {
                    AndroidView(
                        factory = { context ->
                          PlanetSurfaceView(context, selectedPlanet).also { planetSurfaceView = it }
                        },
                        modifier = Modifier.size(600.dp))
                  }

              // LaunchedEffect to update the planet when selectedPlanet changes
              LaunchedEffect(selectedPlanet) { planetSurfaceView?.updatePlanet(selectedPlanet) }
            }
      })
}
