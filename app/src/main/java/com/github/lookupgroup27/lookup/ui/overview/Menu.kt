package com.github.lookupgroup27.lookup.ui.overview

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MenuScreen(navigationActions: NavigationActions) {
  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.MENU)
      },
      modifier = Modifier.testTag("menu_screen")) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
          // Blurred map screen as the background
          Image(
              painter =
                  painterResource(id = R.drawable.landing_screen_bckgrnd), // Import your image
              contentDescription = "Background",
              modifier = Modifier.fillMaxSize().testTag("background_image").blur(20.dp),
              contentScale = ContentScale.Crop)

          IconButton(
              onClick = { navigationActions.goBack() },
              modifier = Modifier.padding(16.dp).align(Alignment.TopStart).testTag("back_button")) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White)
              }
          IconButton(
              onClick = { navigationActions.navigateTo(Screen.PROFILE) },
              modifier =
                  Modifier.padding(16.dp).align(Alignment.TopEnd).testTag("profile_button")) {
                Icon(
                    modifier = Modifier.size(56.dp),
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
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
                Button(onClick = { navigationActions.navigateTo(Screen.QUIZ) }) {
                  Text(text = "Quizzes")
                }
                Button(onClick = { navigationActions.navigateTo(Screen.CALENDAR) }) {
                  Text(text = "Calendar")
                }
                Button(onClick = { navigationActions.navigateTo(Screen.SKY_TRACKER) }) {
                  Text(text = "Sky Tracker")
                }
              }
        }
      }
}
