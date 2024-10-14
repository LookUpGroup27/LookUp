package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.runtime.Composable
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.overview.SampleScreen

@Composable
fun ProfileScreen(navigationActions: NavigationActions) {
  SampleScreen(
      screenText = "Profile Screen",
      navigationActions = navigationActions,
      screenTag = "profile_screen",
      backButtonTag = "go_back_button_profile")
}
