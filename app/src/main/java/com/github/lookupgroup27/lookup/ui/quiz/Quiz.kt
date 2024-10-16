package com.github.lookupgroup27.lookup.ui.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen

@Composable
fun QuizScreen(navigationActions: NavigationActions) {
  Box(
      modifier = Modifier.fillMaxSize().testTag("quiz_screen"),
      contentAlignment = Alignment.Center,
  ) {

    // Background Image
    Image(
        painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize().blur(10.dp).testTag("quiz_background"))

    IconButton(
        onClick = { navigationActions.goBack() },
        modifier =
            Modifier.padding(16.dp).align(Alignment.TopStart).testTag("go_back_button_quiz")) {
          Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Back",
              tint = Color.White)
        }

    // Overlay for content
    Column(
        modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)) {
          // Title Text
          Text(
              text = "Take a Quiz",
              color = Color.White,
              style =
                  MaterialTheme.typography.displaySmall.copy(
                      fontWeight = FontWeight.Bold, fontSize = 32.sp),
              modifier = Modifier.testTag("quiz_title"))

          // Earth Button
          Button(
              onClick = { navigationActions.navigateTo(Screen.QUIZ_PLAY) },
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.fillMaxWidth().height(56.dp).testTag("earth_button")) {
                Text(
                    text = "Earth",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold)
              }

          // Solar System Button
          Button(
              onClick = { navigationActions.navigateTo(Screen.QUIZ_PLAY) },
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.fillMaxWidth().height(56.dp).testTag("solar_system_button")) {
                Text(
                    text = "Solar System",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold)
              }
        }
  }
}
