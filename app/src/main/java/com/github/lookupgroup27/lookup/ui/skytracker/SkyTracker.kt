package com.github.lookupgroup27.lookup.ui.skytracker

import androidx.compose.runtime.Composable
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.overview.SampleScreen

@Composable
fun SkyTrackerScreen(navigationActions: NavigationActions) {
  SampleScreen(
      screenText = "Sky Tracker Screen",
      navigationActions = navigationActions,
      screenTag = "skyTracker_screen",
      backButtonTag = "go_back_button_skyTracker")
}
