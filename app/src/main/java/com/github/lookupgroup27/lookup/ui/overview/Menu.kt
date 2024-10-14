package com.github.lookupgroup27.lookup.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.ui.map.MapScreen
import com.github.lookupgroup27.lookup.ui.navigation.*

@Composable
fun MenuScreen(navigationActions: NavigationActions) {
  Box(modifier = Modifier.fillMaxSize().testTag("menu_screen")) {
    // Blurred map screen as the background
    MapScreen(navigationActions) // Empty function since the back button isn't needed in the blurred
    // background
    Spacer(
        modifier =
            Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .blur(70.dp) // Adjust blur strength here
        )
    IconButton(
        onClick = { navigationActions.goBack() },
        modifier = Modifier.padding(16.dp).align(Alignment.TopStart).testTag("back_button")) {
          Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Back",
              tint = Color.White)
        }

    // Buttons in the foreground
    Column(
        modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
          Text(
              text = "Welcome !",
              color = Color.White,
              style = MaterialTheme.typography.displaySmall)
          Button(onClick = { navigationActions.navigateTo(Screen.QUIZ) }) { Text(text = "Quizzes") }
          Button(onClick = { navigationActions.navigateTo(Screen.CALENDAR) }) {
            Text(text = "Calendar")
          }
          Button(onClick = { navigationActions.navigateTo(Screen.SKY_TRACKER) }) {
            Text(text = "Sky Tracker")
          }
        }
  }
}
