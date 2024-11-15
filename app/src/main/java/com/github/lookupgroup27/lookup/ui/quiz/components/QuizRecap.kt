package com.github.lookupgroup27.lookup.ui.quiz.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.lookupgroup27.lookup.model.quiz.QuizQuestion
import com.github.lookupgroup27.lookup.ui.theme.*

/**
 * Displays a recap of the quiz questions with user's answers, showing correct and incorrect
 * responses.
 *
 * @param questions The list of quiz questions answered by the user.
 * @param userAnswers The list of answers provided by the user.
 */
@Composable
fun QuizRecap(questions: List<QuizQuestion>, userAnswers: List<String>) {
  LazyColumn(
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 8.dp)
              .testTag("quiz_recap"),
      verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(questions.size) { index ->
          val question = questions[index]
          val isCorrect = question.correctAnswer == userAnswers[index]
          Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Icon(
                    imageVector = if (isCorrect) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = if (isCorrect) "Correct" else "Incorrect",
                    tint = if (isCorrect) CorrectAnswerColor else IncorrectAnswerColor,
                    modifier = Modifier.size(24.dp))

                Column {
                  Text(
                      text = "Q${index + 1}: ${question.question}",
                      fontWeight = FontWeight.Bold,
                      fontSize = 16.sp,
                      color = Color.White)
                  Text(
                      text = "Your answer: ${userAnswers[index]}",
                      fontSize = 14.sp,
                      color = if (isCorrect) CorrectAnswerColor else IncorrectAnswerColor)
                }
              }
        }
      }
}
