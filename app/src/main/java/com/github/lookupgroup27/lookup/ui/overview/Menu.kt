package com.github.lookupgroup27.lookup.ui.overview

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.*
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MenuScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current
  val auth = remember { FirebaseAuth.getInstance() }
  val isLoggedIn = auth.currentUser != null
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
              painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
              contentDescription = "Background",
              modifier = Modifier.fillMaxSize().testTag("background_image").blur(20.dp),
              contentScale = ContentScale.Crop)

          // Profile button at the top right
          IconButton(
              onClick = {
                if (isLoggedIn) {
                  navigationActions.navigateTo(Screen.PROFILE)
                } else {
                  navigationActions.navigateTo(Screen.AUTH)
                }
              },
              modifier =
                  Modifier.padding(16.dp).align(Alignment.TopEnd).testTag("profile_button")) {
                Icon(
                    modifier = Modifier.size(56.dp),
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.White)
              }

          // Main content with scrollable column
          Column(
              modifier =
                  Modifier.align(Alignment.Center)
                      .padding(horizontal = 32.dp)
                      .verticalScroll(rememberScrollState()) // Ensuring scroll semantics
                      .testTag("scrollable_menu_content"), // Add unique tag for testing if needed
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Welcome !",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navigationActions.navigateTo(Screen.QUIZ) }) {
                  Text(text = "Quizzes", style = MaterialTheme.typography.headlineSmall)
                }
                Button(onClick = { navigationActions.navigateTo(Screen.CALENDAR) }) {
                  Text(text = "Calendar", style = MaterialTheme.typography.headlineSmall)
                }
                Button(onClick = { navigationActions.navigateTo(Screen.SKY_TRACKER) }) {
                  Text(text = "Sky Tracker", style = MaterialTheme.typography.headlineSmall)
                }
              }
        }
      }
}
