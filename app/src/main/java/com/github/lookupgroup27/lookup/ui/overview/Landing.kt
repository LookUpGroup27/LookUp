package com.github.lookupgroup27.lookup.ui.overview

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.TopLevelDestinations

/**
 * LandingScreen displays the main landing page of the app. It includes a background image, logo,
 * and a button with a home icon. The home button navigates to the "Menu" screen and the background
 * is clickable to navigate to the "Map" screen.
 *
 * @param navController The NavController to handle navigation between screens.
 */
@Composable
fun LandingScreen(navController: NavController) {
  Box(
      modifier =
          Modifier.fillMaxSize().clickable {
            navController.navigate(TopLevelDestinations.MAP.route)
          },
      contentAlignment = Alignment.Center) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize())

        // Center logo image
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              // Logo Image
              Image(
                  painter = painterResource(id = R.drawable.logo_round),
                  contentDescription = "Look Up Logo",
                  modifier = Modifier.size(200.dp),
                  contentScale = ContentScale.Fit)
            }

        // Home Button at the Bottom
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 32.dp),
            contentAlignment = Alignment.BottomCenter) {
              Button(
                  onClick = { navController.navigate("Menu") },
                  modifier = Modifier.padding(16.dp).size(100.dp).testTag("Home Icon"),
                  shape = CircleShape,
                  colors = ButtonDefaults.buttonColors(containerColor = Color.White),
              ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home Icon",
                    tint = Color.Black,
                    modifier = Modifier.size(64.dp))
              }
            }
      }
}

@Preview(showBackground = true)
@Composable
fun PreviewLandingScreen() {
  LandingScreen(navController = rememberNavController())
}
