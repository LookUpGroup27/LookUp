package com.github.lookupgroup27.lookup.ui.quiz

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.quiz.QuizQuestion
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.ui.theme.DarkPurple

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun QuizPlayScreen(viewModel: QuizViewModel, navigationActions: NavigationActions) {

  val quizQuestions by viewModel.quizQuestions.collectAsState()
  val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
  val score by viewModel.score.collectAsState()
  val showScore by viewModel.showScore.collectAsState()
  val selectedAnswer by viewModel.selectedAnswer.collectAsState()
  val quizTitle = viewModel.quizTitle

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    Image(
        painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize().blur(8.dp).testTag("quiz_background"))

    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top) {
          if (!showScore) {
            Button(
                onClick = {
                  viewModel.resetQuiz()
                  navigationActions.navigateTo(Route.QUIZ)
                },
                modifier = Modifier.align(Alignment.Start).padding(top = 8.dp),
                colors =
                    androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color.Red, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp)) {
                  Text(text = "Leave Quiz", textAlign = TextAlign.Center, fontSize = 16.sp)
                }
          }

          Spacer(modifier = Modifier.height(24.dp))

          Text(
              text = "$quizTitle Quiz",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 30.sp,
              modifier = Modifier.padding(vertical = 8.dp).testTag("quiz_title"),
              textAlign = TextAlign.Center)

          Spacer(modifier = Modifier.height(30.dp))

          if (!showScore) {
            quizQuestions.getOrNull(currentQuestionIndex)?.let { question ->
              Text(
                  text = question.question,
                  color = Color.White,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  modifier =
                      Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("quiz_question"),
                  textAlign = TextAlign.Center)

              Spacer(modifier = Modifier.height(20.dp))

              Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                question.answers.forEachIndexed { index, answer ->
                  val backgroundColor =
                      if (selectedAnswer == answer) Color(0xFFFF731F) else DarkPurple
                  Button(
                      onClick = { viewModel.onAnswerSelected(answer) },
                      colors =
                          androidx.compose.material3.ButtonDefaults.buttonColors(
                              containerColor = backgroundColor),
                      modifier =
                          Modifier.fillMaxWidth().height(60.dp).testTag("answer_button_$index")) {
                        Text(text = answer, fontSize = 16.sp, textAlign = TextAlign.Center)
                      }
                }
              }

              Spacer(modifier = Modifier.height(40.dp))

              Button(
                  onClick = { viewModel.goToNextQuestion() },
                  enabled = selectedAnswer != null,
                  colors =
                      androidx.compose.material3.ButtonDefaults.buttonColors(
                          containerColor =
                              if (selectedAnswer != null) Color(0xFF00C853) else Color(0xFF6A9605),
                          contentColor = Color.White),
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(vertical = 8.dp)
                          .height(60.dp)
                          .testTag("next_button")) {
                    Text(text = "Next Question", fontSize = 18.sp)
                  }
            }
          } else {
            Text(
                text = "Your score: $score/${quizQuestions.size}",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("score_text"))

            Box(modifier = Modifier.height(600.dp).fillMaxWidth()) {
              QuizRecap(viewModel.getQuestions(), viewModel.getUserAnswers())

              Box(
                  modifier =
                      Modifier.align(Alignment.BottomCenter)
                          .fillMaxWidth()
                          .height(20.dp)
                          .background(
                              brush =
                                  Brush.verticalGradient(
                                      colors =
                                          listOf(
                                              Color.Transparent, Color.Black.copy(alpha = 0.6f)))))
            }

            Text(
                text = "Scroll to see more",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp))
          }
        }

    if (showScore) {
      Box(
          modifier = Modifier.fillMaxSize().padding(bottom = 16.dp),
          contentAlignment = Alignment.BottomCenter) {
            Button(
                onClick = {
                  viewModel.resetQuiz()
                  navigationActions.navigateTo(Route.QUIZ)
                },
                modifier =
                    Modifier.fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 24.dp)
                        .testTag("return_to_quiz_selection_button")) {
                  Text(
                      text = "Return to Quiz Selection",
                      fontSize = 18.sp,
                      textAlign = TextAlign.Center)
                }
          }
    }
  }
}

@Composable
fun QuizRecap(questions: List<QuizQuestion>, userAnswers: List<String>) {
  LazyColumn(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
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
                    tint = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
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
                      color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336))
                }
              }
        }
      }
}
