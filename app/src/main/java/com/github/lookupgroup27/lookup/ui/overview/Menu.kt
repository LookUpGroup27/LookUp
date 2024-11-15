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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.*
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MenuScreen(navigationActions: NavigationActions) {

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
                    modifier = Modifier.size(1000.dp),
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.White)
              }

          // Main content with scrollable column
          Column(
              modifier =
                  Modifier.align(Alignment.Center)
                      .padding(horizontal = 32.dp)
                      .verticalScroll(rememberScrollState())
                      .testTag("scrollable_menu_content"),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "App Logo",
                    modifier =
                        Modifier.size(250.dp)
                            .align(Alignment.CenterHorizontally)
                            .testTag("app_logo"))

                Button(
                    onClick = { navigationActions.navigateTo(Screen.QUIZ) },
                    modifier = Modifier.fillMaxWidth(0.6f)) {
                      Text(
                          text = "Quizzes",
                          style = MaterialTheme.typography.headlineSmall,
                          fontWeight = FontWeight.Bold)
                    }
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navigationActions.navigateTo(Screen.CALENDAR) },
                    modifier = Modifier.fillMaxWidth(0.6f)) {
                      Text(
                          text = "Calendar",
                          style = MaterialTheme.typography.headlineSmall,
                          fontWeight = FontWeight.Bold)
                    }
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navigationActions.navigateTo(Screen.GOOGLE_MAP) },
                    modifier = Modifier.fillMaxWidth(0.6f)) {
                      Text(
                          text = "Google Map",
                          style = MaterialTheme.typography.headlineSmall,
                          fontWeight = FontWeight.Bold)
                    }
              }
        }
      }
}
