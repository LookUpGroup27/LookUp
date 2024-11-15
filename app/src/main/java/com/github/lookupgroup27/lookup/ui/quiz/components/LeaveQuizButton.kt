package com.github.lookupgroup27.lookup.ui.quiz.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Displays a button to allow the user to leave the quiz and return to the quiz selection screen.
 *
 * @param onClick The action to perform when the button is clicked.
 * @param modifier Modifier for styling and positioning of the button.
 */
@Composable
fun LeaveQuizButton(onClick: () -> Unit, modifier: Modifier) {
  Button(
      onClick = onClick,
      modifier = modifier,
      colors =
          androidx.compose.material3.ButtonDefaults.buttonColors(
              containerColor = Color.Red, contentColor = Color.White),
      shape = RoundedCornerShape(8.dp)) {
        Text(text = "Leave Quiz", textAlign = TextAlign.Center, fontSize = 16.sp)
      }
}
