package com.github.lookupgroup27.lookup.ui.overview

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun LandingScreen(navigationActions: NavigationActions) {
  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

  // Background container with clickable modifier to navigate to Map screen
  BoxWithConstraints(
      modifier =
          Modifier.fillMaxSize().clickable {
            navigationActions.navigateTo(Screen.MAP)
          } // Add clickable modifier here
      ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize())

        // Content Layout based on Orientation
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally) {
              // Top Prompt Text
              Text(
                  text = "Click for full map view",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(top = if (isLandscape) 16.dp else 32.dp))

              // Centered Logo Image
              Image(
                  painter = painterResource(id = R.drawable.app_logo),
                  contentDescription = "Look Up Logo",
                  modifier =
                      Modifier.size(if (isLandscape) 180.dp else 250.dp)
                          .align(Alignment.CenterHorizontally),
                  contentScale = ContentScale.Fit)

              // Bottom Home Button
              Button(
                  onClick = { navigationActions.navigateTo(Screen.MENU) },
                  modifier = Modifier.padding(16.dp).size(if (isLandscape) 130.dp else 160.dp),
                  shape = CircleShape,
                  colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                    Icon(
                        painter = painterResource(id = R.drawable.home_button),
                        contentDescription = "Home Icon",
                        tint = Color.Unspecified,
                        modifier = Modifier.fillMaxSize())
                  }
            }
      }
}

@Preview(showBackground = true)
@Composable
fun PreviewLandingScreen() {
  LandingScreen(NavigationActions(rememberNavController()))
}
