package com.github.lookupgroup27.lookup.ui.overview

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.theme.StarLightWhite
import com.github.lookupgroup27.lookup.util.NetworkUtils
import com.github.lookupgroup27.lookup.util.ToastHelper
import components.BackgroundImage

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun LandingScreen(
    navigationActions: NavigationActions,
    toastHelper: ToastHelper = ToastHelper(LocalContext.current)
) {

  val context = LocalContext.current

  // Lock the screen orientation to portrait mode.
  DisposableEffect(Unit) {
    val activity = context as? ComponentActivity
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
  }

  val isOnline = remember { mutableStateOf(NetworkUtils.isNetworkAvailable(context)) }

  // Background container with clickable modifier to navigate to Map screen
  BoxWithConstraints(
      modifier =
          Modifier.fillMaxSize().testTag("LandingScreen").clickable {
            if (isOnline.value) navigationActions.navigateTo(Screen.SKY_MAP)
            else toastHelper.showNoInternetToast()
          }) {
        // Background Image
        BackgroundImage(
            painterResId = R.drawable.landscape_background,
            contentDescription = stringResource(R.string.background_description),
        )

        // Content Layout with Vertical Scrolling
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()) // Enable vertical scrolling in both modes
                    .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.height(25.dp))
              // Top Prompt Text
              Text(
                  text = "Click for full map view",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Normal,
                  color = StarLightWhite,
                  modifier = Modifier.padding(top = 32.dp))

              // Centered Logo Image
              Image(
                  painter = painterResource(id = R.drawable.app_logo),
                  contentDescription = "Look Up Logo",
                  modifier = Modifier.size(250.dp),
                  contentScale = ContentScale.Fit)

              // Bottom Home Button
              Button(
                  onClick = { navigationActions.navigateTo(Screen.MENU) },
                  modifier = Modifier.padding(16.dp).size(160.dp).testTag("Home Icon"),
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
