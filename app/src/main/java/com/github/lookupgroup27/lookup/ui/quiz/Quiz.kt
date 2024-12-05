package com.github.lookupgroup27.lookup.ui.quiz

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.quiz.components.QuizThemeButton
import components.BackgroundImage

/**
 * Composable function that displays the main screen for selecting a quiz. This screen includes a
 * background image, title, and a list of quiz themes with associated best scores. Users can
 * navigate back to the menu screen or choose a quiz to start.
 *
 * @param viewModel The QuizViewModel that manages the quiz data and state.
 * @param navigationActions Provides navigation actions to move between screens.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun QuizScreen(viewModel: QuizViewModel, navigationActions: NavigationActions) {

  val context = LocalContext.current
  val bestScores = viewModel.getAllBestScores()

  BoxWithConstraints(modifier = Modifier.fillMaxSize().testTag("quiz_screen")) {
    // Background Image
    BackgroundImage(
        painterResId = R.drawable.background_blurred,
        contentDescription = stringResource(R.string.background_description),
        testTag = stringResource(R.string.background_test_tag))

    // Back Button
    IconButton(
        onClick = { navigationActions.navigateTo(Screen.MENU) },
        modifier =
            Modifier.padding(16.dp).align(Alignment.TopStart).testTag("go_back_button_quiz")) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White)
        }

    // Quiz Content with Vertical Scrolling
    Column(
        modifier =
            Modifier.padding(
                    vertical = 88.dp, horizontal = 25.dp) // General padding for both orientations
                .verticalScroll(
                    rememberScrollState()), // Enable vertical scrolling in all orientations
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)) {
          // Title
          Text(
              text = "Take a Quiz",
              color = Color.White,
              style =
                  MaterialTheme.typography.displaySmall.copy(
                      fontWeight = FontWeight.Bold, fontSize = 32.sp),
              modifier = Modifier.testTag("quiz_title"))

          // Quiz Options
          bestScores.toSortedMap().forEach { (theme, score) ->
            QuizThemeButton(
                theme = theme,
                bestScore = "$score",
                onClick = {
                  viewModel.loadQuizDataForTheme(theme, context)
                  navigationActions.navigateTo(Screen.QUIZ_PLAY)
                },
                testTag = "${theme.lowercase()}_button")
          }
        }
  }
}
