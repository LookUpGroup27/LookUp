package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen

/**
 * A Composable function that renders the Profile screen, which includes:
 * - A profile icon at the top center of the screen.
 * - Two buttons: "Personal Info" and "Your Collection," both centered below the profile icon.
 * - A bottom navigation menu to navigate between different sections (e.g., Map, Menu).
 *
 * The buttons trigger navigation actions
 * when clicked, allowing users to navigate to the Profile or Collection screens.
 *
 * @param navigationActions An instance of [NavigationActions] that defines the navigation behavior.
 *                          It handles navigating between the Profile, Collection, and other
 *                          top-level sections of the app through the bottom navigation menu.
 */


@Composable
fun ProfileScreen(navigationActions: NavigationActions) {
  // Column to stack the profile section and bottom navigation
  Column(
      modifier =
          Modifier.fillMaxSize().background(Color.DarkGray), // Background for the entire screen
      verticalArrangement =
          Arrangement.SpaceBetween // Ensure items are spaced between top and bottom
      ) {
        // Center the Profile Icon and Buttons in the middle of the screen
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 300.dp), // Padding from the top
            horizontalAlignment = Alignment.CenterHorizontally // Center content inside the column
            ) {
              // Profile Icon
              Icon(
                  imageVector = Icons.Default.AccountCircle,
                  contentDescription = "Profile Icon",
                  tint = Color.White,
                  modifier = Modifier.size(150.dp))

              // Spacer between profile icon and first button
              Spacer(modifier = Modifier.height(60.dp))

              // Personal Info Button
              Button(
                  onClick = { navigationActions.navigateTo(Screen.PROFILE) },
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.primary,
                          contentColor = Color.White),
                  modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = "Personal Info", fontSize = 18.sp)
                  }

              // Collection Button
              Button(
                  onClick = { navigationActions.navigateTo(Screen.COLLECTION) },
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.primary, // Solid button color
                          contentColor = Color.White),
                  modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = "Your Collection", fontSize = 18.sp)
                  }
            }

        // Bottom Navigation
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      }
}

/**
 * A Composable function that previews the Profile screen.
 *
 * This function is used for design-time previews in the Android Studio editor. It renders
 * the ProfileScreen composable without requiring live navigation actions, and provides a
 * quick look at how the Profile screen will appear.
 */

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  ProfileScreen(navigationActions)
}
