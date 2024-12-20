package com.github.lookupgroup27.lookup.ui.quiz.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.lookupgroup27.lookup.ui.theme.LightPurple
import com.github.lookupgroup27.lookup.ui.theme.StarLightWhite

/**
 * Button component to represent each quiz theme with its best score.
 *
 * @param theme The name of the quiz theme to display on the button.
 * @param bestScore The user's best score for the given theme, displayed on the button.
 * @param onClick Lambda function to handle button click events.
 * @param testTag A tag used for testing purposes, helping to identify this component in tests.
 */
@Composable
fun QuizThemeButton(theme: String, bestScore: String, onClick: () -> Unit, testTag: String) {
  Button(
      onClick = onClick,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier.fillMaxWidth().height(56.dp).testTag(testTag),
      colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = LightPurple),
      border = BorderStroke(0.3.dp, StarLightWhite)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
              Text(
                  text = theme,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = StarLightWhite,
                  modifier = Modifier.padding(start = 4.dp))
              Text(
                  text = "Best Score: $bestScore/15",
                  fontSize = 13.sp,
                  fontStyle = FontStyle.Italic,
                  fontWeight = FontWeight.Normal,
                  color = StarLightWhite.copy(alpha = 0.7f),
              )
            }
      }
}
