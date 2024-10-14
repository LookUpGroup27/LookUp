package com.github.lookupgroup27.lookup.ui.overview

import androidx.compose.runtime.Composable
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions

@Composable
fun QuizScreen(navigationActions: NavigationActions) {
  SampleScreen(
      screenText = "Quiz Screen",
      navigationActions = navigationActions,
      screenTag = "quiz_screen",
      backButtonTag = "go_back_button_quiz")
}
