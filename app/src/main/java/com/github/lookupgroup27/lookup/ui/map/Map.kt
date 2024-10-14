package com.github.lookupgroup27.lookup.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions

@Composable
fun MapScreen(navigationActions: NavigationActions) {
  Box(modifier = Modifier.fillMaxSize().testTag("map_screen")) {
    Image(
        painter = painterResource(id = R.drawable.landing_screen_bckgrnd), // Import your image
        contentDescription = null,
        modifier = Modifier.fillMaxSize().testTag("map_background"),
        contentScale = ContentScale.Crop)

    // Back button
    IconButton(
        onClick = { navigationActions.goBack() },
        modifier = Modifier.padding(16.dp).align(Alignment.TopStart).testTag("go_back_button")) {
          Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Back",
              tint = Color.White)
        }
  }
}
