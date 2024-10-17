package com.github.lookupgroup27.lookup.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

@Composable
fun QuizPlayScreen(viewModel: QuizViewModel, navigationActions: NavigationActions) {
  val quizQuestions by viewModel.quizQuestions.observeAsState(emptyList())
  val currentQuestionIndex by viewModel.currentQuestionIndex.observeAsState(0)
  val selectedAnswer by viewModel.selectedAnswer.observeAsState(null)
  val score by viewModel.score.observeAsState(0)
  val showScore by viewModel.showScore.observeAsState(false)
  val quizTitle by viewModel.quizTitle.observeAsState("")

  Box(modifier = Modifier.fillMaxSize()) {
    Image(
        painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize().blur(10.dp).testTag("quiz_background"))

    Button(
        onClick = {
          viewModel.resetQuiz()
          navigationActions.navigateTo(Route.QUIZ)
        },
        modifier = Modifier.padding(16.dp).align(Alignment.TopStart),
        colors =
            androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.Red, contentColor = Color.White),
        shape = RoundedCornerShape(8.dp)) {
          Text("Leave Quiz", textAlign = TextAlign.Center)
        }

    Text(
        text = "$quizTitle Quiz",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        modifier =
            Modifier.align(Alignment.TopCenter)
                .padding(horizontal = 16.dp)
                .padding(top = 100.dp)
                .testTag("quiz_title"),
        maxLines = 1,
        softWrap = true)

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 130.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
          if (showScore) {
            Spacer(modifier = Modifier.height(50.dp))
            Column {
              Text(
                  text = "Your score: $score/${quizQuestions.size}",
                  color = Color.White,
                  style =
                      MaterialTheme.typography.bodyLarge.copy(
                          fontWeight = FontWeight.Bold, fontSize = 30.sp),
                  modifier = Modifier.align(Alignment.CenterHorizontally).testTag("score_text"))

              Spacer(modifier = Modifier.height(30.dp))

              Button(
                  onClick = {
                    viewModel.resetQuiz()
                    navigationActions.navigateTo(Route.QUIZ)
                  },
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = 16.dp, vertical = 10.dp)
                          .height(56.dp)
                          .testTag("return_to_quiz_selection_button")) {
                    Text(
                        "Return to Quiz Selection",
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center)
                  }
            }
          } else {
            val question = quizQuestions.getOrNull(currentQuestionIndex)

            if (question != null) {
              Text(
                  text = question.question,
                  color = Color.White,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("quiz_question"))

              Spacer(modifier = Modifier.height(26.dp))

              question.answers.forEachIndexed { index, answer ->
                val backgroundColor =
                    if (selectedAnswer == answer) Color(0xffff731f) else DarkPurple
                Button(
                    onClick = { viewModel.onAnswerSelected(answer) },
                    colors =
                        androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = backgroundColor),
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .height(35.dp)
                            .testTag("answer_button_$index")) {
                      Text(
                          text = answer,
                          fontSize = 18.sp,
                          modifier = Modifier.fillMaxWidth(),
                          textAlign = TextAlign.Center)
                    }
              }

              Spacer(modifier = Modifier.height(20.dp))

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
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                                .height(35.dp)
                                .testTag("next_button")) {
                          Text(text = "Next Question", fontSize = 18.sp)
                        }
                  }
            }
          }
        }
  }
}
