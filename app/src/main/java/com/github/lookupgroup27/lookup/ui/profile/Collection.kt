package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.runtime.Composable
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.overview.SampleScreen

/**
 * A simple placeholder Composable function for the Collection screen. This will prevent errors when
 * navigating to the "Your Collection" section. It will be implemented with real functionality
 * later.
 */
@Composable
fun CollectionScreen(navigationActions: NavigationActions) {
  SampleScreen(
      screenText = "Collection Screen",
      navigationActions = navigationActions,
      screenTag = "collection_screen",
      backButtonTag = "go_back_button_collection")
}
