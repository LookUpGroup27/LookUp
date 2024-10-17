package com.github.lookupgroup27.lookup.ui.profile

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.overview.SampleScreen

/**
 * A simple placeholder Composable function for the Collection screen. This will prevent errors when
 * navigating to the "Your Collection" section. It will be implemented with real functionality
 * later.
 */
@Composable
fun CollectionScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current

  // Show a Toast message when the CollectionScreen is displayed
  LaunchedEffect(Unit) {
    Toast.makeText(context, "Collection screen is not yet implemented", Toast.LENGTH_SHORT).show()
  }

  SampleScreen(
      screenText = "Collection Screen",
      navigationActions = navigationActions,
      screenTag = "collection_screen",
      backButtonTag = "go_back_button_collection")
}
