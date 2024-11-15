package com.github.lookupgroup27.lookup.ui.quiz.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.lookupgroup27.lookup.ui.theme.NextButtonDisabledColor
import com.github.lookupgroup27.lookup.ui.theme.NextButtonEnabledColor

/**
 * Composable function to display a "Next Question" button in the quiz.
 *
 * @param enabled Determines if the button is clickable or disabled based on answer selection.
 * @param onClick Lambda function to handle the button's click action, typically advancing to the
 *   next question.
 */
@Composable
fun NextButton(enabled: Boolean, onClick: () -> Unit) {
  Button(
      onClick = onClick,
      enabled = enabled,
      colors =
          androidx.compose.material3.ButtonDefaults.buttonColors(
              containerColor = if (enabled) NextButtonEnabledColor else NextButtonDisabledColor,
              contentColor = Color.White),
      modifier =
          Modifier.fillMaxWidth().padding(vertical = 8.dp).height(60.dp).testTag("next_button")) {
        Text(text = "Next Question", fontSize = 18.sp)
      }
}
