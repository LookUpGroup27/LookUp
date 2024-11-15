package com.github.lookupgroup27.lookup.ui.quiz.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Displays an answer button that the user can select as their response to the quiz question.
 *
 * @param answer The answer text displayed on the button.
 * @param backgroundColor The background color of the button, which may change based on the
 *   selection state.
 * @param onClick The action to perform when the button is clicked.
 * @param index The index of the answer, used for tagging and identifying.
 */
@Composable
fun AnswerButton(answer: String, backgroundColor: Color, onClick: () -> Unit, index: Int) {
  Button(
      onClick = onClick,
      colors =
          androidx.compose.material3.ButtonDefaults.buttonColors(
              containerColor = backgroundColor, contentColor = Color.White),
      modifier = Modifier.fillMaxWidth().height(60.dp).testTag("answer_button_$index")) {
        Text(text = answer, fontSize = 16.sp, textAlign = TextAlign.Center)
      }
}
