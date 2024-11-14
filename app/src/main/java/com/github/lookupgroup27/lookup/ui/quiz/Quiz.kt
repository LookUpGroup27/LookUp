package com.github.lookupgroup27.lookup.ui.quiz

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun QuizScreen(viewModel: QuizViewModel, navigationActions: NavigationActions) {

  val context = LocalContext.current
  val bestScores = viewModel.getAllBestScores()

  BoxWithConstraints(modifier = Modifier.fillMaxSize().testTag("quiz_screen")) {
    // Background Image
    Image(
        painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize().blur(10.dp).testTag("quiz_background"))

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
            QuizOptionButton(
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

@Composable
fun QuizOptionButton(theme: String, bestScore: String, onClick: () -> Unit, testTag: String) {
  Button(
      onClick = onClick,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier.fillMaxWidth().height(56.dp).testTag(testTag),
      colors =
          androidx.compose.material3.ButtonDefaults.buttonColors(
              containerColor = Color(0xFF4E5DAB))) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
              Text(
                  text = theme,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(start = 4.dp))
              Text(
                  text = "Best Score: $bestScore/15",
                  fontSize = 14.sp,
                  fontStyle = FontStyle.Italic,
                  fontWeight = FontWeight.Normal,
                  color = Color(0xFFDADADA))
            }
      }
}
