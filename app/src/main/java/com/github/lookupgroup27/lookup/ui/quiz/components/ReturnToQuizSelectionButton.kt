package com.github.lookupgroup27.lookup.ui.quiz.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Displays a button allowing the user to return to the quiz selection screen after completing the
 * quiz.
 *
 * @param onClick The action to perform when the button is clicked.
 */
@Composable
fun ReturnToQuizSelectionButton(onClick: () -> Unit) {
  Box(
      modifier = Modifier.fillMaxSize().padding(bottom = 16.dp),
      contentAlignment = Alignment.BottomCenter) {
        Button(
            onClick = onClick,
            modifier =
                Modifier.fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp)
                    .testTag("return_to_quiz_selection_button")) {
              Text(
                  text = "Return to Quiz Selection", fontSize = 18.sp, textAlign = TextAlign.Center)
            }
      }
}
