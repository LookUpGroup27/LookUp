package com.github.lookupgroup27.lookup.ui.quiz.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuestionText(questionText: String) {
  Text(
      text = questionText,
      color = Color.White,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("quiz_question"),
      textAlign = TextAlign.Center)
}
