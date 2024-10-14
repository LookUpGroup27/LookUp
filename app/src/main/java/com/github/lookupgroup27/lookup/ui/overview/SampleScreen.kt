package com.github.lookupgroup27.lookup.ui.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions

@Composable
fun SampleScreen(
    screenText: String,
    navigationActions: NavigationActions,
    screenTag: String,
    backButtonTag: String
) {
  Box(
      modifier =
          Modifier.fillMaxSize()
              .padding(16.dp) // Padding for the entire box
              .testTag(screenTag)) {
        // Back button
        IconButton(
            onClick = { navigationActions.goBack() },
            modifier = Modifier.align(Alignment.TopStart).testTag(backButtonTag)) {
              Icon(
                  imageVector = Icons.Default.ArrowBack,
                  contentDescription = "Back",
                  tint = Color.Black)
            }

        // Screen-specific text in the center of the screen
        Text(
            text = screenText,
            color = Color.Black,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.align(Alignment.Center))
      }
}
