package com.github.lookupgroup27.lookup.ui.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.quiz.QuizViewModel
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen

@Composable
fun QuizScreen(viewModel: QuizViewModel, navigationActions: NavigationActions) {

  val context = LocalContext.current

  Box(
      modifier = Modifier.fillMaxSize().testTag("quiz_screen"),
      contentAlignment = Alignment.Center,
  ) {
    Image(
        painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize().blur(10.dp).testTag("quiz_background"))

    IconButton(
        onClick = { navigationActions.navigateTo(Screen.MENU) },
        modifier =
            Modifier.padding(16.dp).align(Alignment.TopStart).testTag("go_back_button_quiz")) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White)
        }

    Column(
        modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)) {
          Text(
              text = "Take a Quiz",
              color = Color.White,
              style =
                  MaterialTheme.typography.displaySmall.copy(
                      fontWeight = FontWeight.Bold, fontSize = 32.sp),
              modifier = Modifier.testTag("quiz_title"))

          QuizOptionButton(
              text = "Earth",
              onClick = {
                viewModel.loadQuizDataForTheme("Earth", context)
                navigationActions.navigateTo(Screen.QUIZ_PLAY)
              },
              "earth_button")

          QuizOptionButton(
              text = "Solar System",
              onClick = {
                viewModel.loadQuizDataForTheme("Solar System", context)
                navigationActions.navigateTo(Screen.QUIZ_PLAY)
              },
              "solar_system_button")
        }
  }
}

@Composable
fun QuizOptionButton(text: String, onClick: () -> Unit, testTag: String) {
  Button(
      onClick = onClick,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier.fillMaxWidth().height(56.dp).testTag(testTag)) {
        Text(text = text, fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
      }
}
