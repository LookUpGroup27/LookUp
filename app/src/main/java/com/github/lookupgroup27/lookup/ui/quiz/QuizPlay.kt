package com.github.lookupgroup27.lookup.ui.quiz

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.github.lookupgroup27.lookup.model.quiz.QuizViewModel
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.ui.theme.DarkPurple

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun QuizPlayScreen(viewModel: QuizViewModel, navigationActions: NavigationActions) {
  // Observing ViewModel data
  val quizQuestions by viewModel.quizQuestions.observeAsState(emptyList())
  val currentQuestionIndex by viewModel.currentQuestionIndex.observeAsState(0)
  val selectedAnswer by viewModel.selectedAnswer.observeAsState(null)
  val score by viewModel.score.observeAsState(0)
  val showScore by viewModel.showScore.observeAsState(false)
  val quizTitle by viewModel.quizTitle.observeAsState("")

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    // Background Image
    Image(
        painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize().blur(8.dp).testTag("quiz_background"))

    // Quiz Content with Vertical Scrolling
    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(
                    rememberScrollState()), // Enable vertical scroll in all orientations
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween) {
          // Leave Quiz Button
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

          // Quiz Title
          Text(
              text = "$quizTitle Quiz",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 30.sp,
              modifier = Modifier.padding(vertical = 8.dp).testTag("quiz_title"),
              textAlign = TextAlign.Center)

          if (showScore) {
            // Score Display
            Text(
                text = "Your score: $score/${quizQuestions.size}",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("score_text"))
            Box(
                modifier =
                    Modifier.height(450.dp) // Limit height of scrollable area
                        .fillMaxWidth()) {
                  QuizRecap(viewModel.getQuestions(), viewModel.getUserAnswers())

                  // Gradient overlay at the bottom
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
                                                  Color.Transparent,
                                                  Color.Black.copy(alpha = 0.6f)))))
                }

            // Add a scroll prompt above the scrollable area
            Text(
                text = "Scroll to see more",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp))

            Button(
                onClick = {
                  viewModel.resetQuiz()
                  navigationActions.navigateTo(Route.QUIZ)
                },
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(56.dp)
                        .testTag("return_to_quiz_selection_button")) {
                  Text(
                      text = "Return to Quiz Selection",
                      fontSize = 18.sp,
                      textAlign = TextAlign.Center)
                }
          } else {
            // Current Question
            quizQuestions.getOrNull(currentQuestionIndex)?.let { question ->
              Text(
                  text = question.question,
                  color = Color.White,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  modifier =
                      Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("quiz_question"),
                  textAlign = TextAlign.Center)

              // Answer Buttons
              Column(
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    question.answers.forEachIndexed { index, answer ->
                      val backgroundColor =
                          if (selectedAnswer == answer) Color(0xFFFF731F) else DarkPurple
                      Button(
                          onClick = { viewModel.onAnswerSelected(answer) },
                          colors =
                              androidx.compose.material3.ButtonDefaults.buttonColors(
                                  containerColor = backgroundColor),
                          modifier =
                              Modifier.fillMaxWidth()
                                  .height(40.dp)
                                  .testTag("answer_button_$index")) {
                            Text(text = answer, fontSize = 16.sp, textAlign = TextAlign.Center)
                          }
                    }
                  }

              // Next Question Button
              AnimatedVisibility(
                  visible = selectedAnswer != null,
                  enter = fadeIn(animationSpec = tween(durationMillis = 400)),
                  exit = fadeOut(animationSpec = tween(durationMillis = 400))) {
                    Button(
                        onClick = { viewModel.goToNextQuestion() },
                        enabled = selectedAnswer != null,
                        colors =
                            androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor =
                                    if (selectedAnswer != null) Color(0xFF00C853)
                                    else Color(0xFF6A9605),
                                contentColor = Color.White),
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .height(40.dp)
                                .testTag("next_button")) {
                          Text(text = "Next Question", fontSize = 18.sp)
                        }
                  }
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
