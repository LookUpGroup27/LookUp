package com.github.lookupgroup27.lookup.ui.quiz

import androidx.compose.runtime.Composable
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.overview.SampleScreen

@Composable
fun QuizPlayScreen(navigationActions: NavigationActions) {
  SampleScreen(
      screenText = "Quiz Play Screen",
      navigationActions = navigationActions,
      screenTag = "quiz_play_screen",
      backButtonTag = "go_back_button_quiz")
}
