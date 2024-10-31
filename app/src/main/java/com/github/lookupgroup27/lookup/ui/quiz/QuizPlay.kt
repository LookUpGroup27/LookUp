package com.github.lookupgroup27.lookup.ui.quiz

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.quiz.QuizViewModel
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.ui.theme.DarkPurple

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun QuizPlayScreen(viewModel: QuizViewModel, navigationActions: NavigationActions) {
  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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

    // Quiz Content
    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(horizontal = if (isLandscape) 32.dp else 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()), // Enable vertical scroll for flexibility
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween) {
          // Leave Quiz Button
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
                Text(
                    text = "Leave Quiz",
                    textAlign = TextAlign.Center,
                    fontSize = if (isLandscape) 12.sp else 16.sp)
              }

          // Quiz Title
          Text(
              text = "$quizTitle Quiz",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = if (isLandscape) 24.sp else 30.sp,
              modifier = Modifier.padding(vertical = 8.dp).testTag("quiz_title"),
              textAlign = TextAlign.Center)

          if (showScore) {
            // Score Display
            Text(
                text = "Your score: $score/${quizQuestions.size}",
                color = Color.White,
                fontSize = if (isLandscape) 20.sp else 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("score_text"))
            Button(
                onClick = {
                  viewModel.resetQuiz()
                  navigationActions.navigateTo(Route.QUIZ)
                },
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(if (isLandscape) 40.dp else 56.dp)
                        .testTag("return_to_quiz_selection_button")) {
                  Text(
                      text = "Return to Quiz Selection",
                      fontSize = if (isLandscape) 14.sp else 18.sp,
                      textAlign = TextAlign.Center)
                }
          } else {
            // Current Question
            quizQuestions.getOrNull(currentQuestionIndex)?.let { question ->
              Text(
                  text = question.question,
                  color = Color.White,
                  fontSize = if (isLandscape) 16.sp else 20.sp,
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
                                  .height(if (isLandscape) 32.dp else 40.dp)
                                  .testTag("answer_button_$index")) {
                            Text(
                                text = answer,
                                fontSize = if (isLandscape) 14.sp else 16.sp,
                                textAlign = TextAlign.Center)
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
                                .height(if (isLandscape) 32.dp else 40.dp)
                                .testTag("next_button")) {
                          Text(text = "Next Question", fontSize = if (isLandscape) 14.sp else 18.sp)
                        }
                  }
            }
          }
        }
  }
}
