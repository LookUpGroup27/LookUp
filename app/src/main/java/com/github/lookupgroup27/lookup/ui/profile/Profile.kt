package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.theme.DarkPurple

/**
 * A Composable function that renders the Profile screen, which includes:
 * - A profile icon at the top center of the screen.
 * - Two buttons: "Personal Info" and "Your Collection," both centered below the profile icon.
 * - A bottom navigation menu to navigate between different sections (e.g., Map, Menu).
 *
 * The buttons trigger navigation actions when clicked, allowing users to navigate to the Profile or
 * Collection screens.
 *
 * @param navigationActions An instance of [NavigationActions] that defines the navigation behavior.
 *   It handles navigating between the Profile, Collection, and other top-level sections of the app
 *   through the bottom navigation menu.
 */
@Composable
fun ProfileScreen(navigationActions: NavigationActions) {
  Scaffold(
      bottomBar = {
        // Custom Bottom Navigation Menu using the bottomBar parameter
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      }) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
          // Background Image
          Image(
              painter = painterResource(id = R.drawable.background_blurred),
              contentDescription = "Background",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize())

          // Profile Icon and Buttons
          Column(
              modifier = Modifier.fillMaxWidth().padding(top = 172.dp), // Padding from the top
              horizontalAlignment = Alignment.CenterHorizontally // Center content inside the column
              ) {
                // Profile Icon
                Icon(
                    painter = painterResource(id = R.drawable.profile_icon),
                    contentDescription = "Profile Icon",
                    modifier = Modifier.size(150.dp),
                    tint = Color.Unspecified // Prevents any default tint color
                    )

                // Spacer between profile icon and first button
                Spacer(modifier = Modifier.height(120.dp))

                // Personal Info Button
                ProfileButton(
                    text = "Personal Info     >",
                    onClick = { navigationActions.navigateTo(Screen.PROFILE_INFORMATION) })

                // Collection Button
                ProfileButton(
                    text = "Your Collection   >",
                    onClick = { navigationActions.navigateTo(Screen.COLLECTION) })
              }
        }
      }
}

/**
 * A Composable function that displays a stylized profile button with rounded corners, shadow, and
 * custom colors. The button is clickable and its action can be defined by passing an `onClick`
 * lambda.
 *
 * @param text The text to be displayed inside the button.
 * @param onClick A lambda function that is invoked when the button is clicked.
 */
@Composable
fun ProfileButton(text: String, onClick: () -> Unit) {
  Button(
      onClick = onClick,
      colors = ButtonDefaults.buttonColors(containerColor = DarkPurple, contentColor = Color.White),
      shape = RoundedCornerShape(174.dp),
      modifier =
          Modifier.width(262.dp)
              .padding(horizontal = 20.dp, vertical = 10.dp)
              .border(
                  width = 1.dp,
                  color = Color.White.copy(alpha = 0.24f),
                  shape = RoundedCornerShape(64.dp))
              .shadow(elevation = 20.dp, shape = RoundedCornerShape(20.dp), clip = true)
              .fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
              Text(
                  text = text,
                  fontSize = 19.sp,
                  fontWeight = FontWeight.W800,
                  modifier = Modifier.weight(1f),
                  textAlign = TextAlign.Center)
            }
      }
}

/**
 * A Composable function that previews the Profile screen.
 *
 * This function is used for design-time previews in the Android Studio editor. It renders the
 * ProfileScreen composable without requiring live navigation actions, and provides a quick look at
 * how the Profile screen will appear.
 */
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  ProfileScreen(navigationActions)
}
