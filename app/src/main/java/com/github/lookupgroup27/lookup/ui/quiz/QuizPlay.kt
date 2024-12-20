package com.github.lookupgroup27.lookup.ui.quiz

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.ui.quiz.components.*
import com.github.lookupgroup27.lookup.ui.theme.AnswerSelectedColor
import com.github.lookupgroup27.lookup.ui.theme.LightPurple

/**
 * Displays the main QuizPlayScreen where the user interacts with questions and navigates through
 * the quiz.
 *
 * @param viewModel The QuizViewModel that holds the state and logic of the quiz.
 * @param navigationActions Navigation actions for screen transitions.
 */
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
        painter = painterResource(id = R.drawable.landscape_background),
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize().blur(10.dp).testTag("quiz_background"))

    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top) {
          if (!showScore) {
            LeaveQuizButton(
                onClick = {
                  viewModel.resetQuiz()
                  navigationActions.navigateTo(Route.QUIZ)
                },
                modifier = Modifier.align(Alignment.Start))
          }

          Spacer(modifier = Modifier.height(24.dp))

          Text(
              text = "$quizTitle Quiz",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 30.sp,
              modifier = Modifier.padding(vertical = 8.dp).testTag("quiz_title"),
              textAlign = TextAlign.Center)

          Spacer(modifier = Modifier.height(20.dp))

          if (!showScore) {
            quizQuestions.getOrNull(currentQuestionIndex)?.let { question ->
              QuestionText(questionText = question.question)

              Spacer(modifier = Modifier.height(20.dp))

              Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                question.answers.forEachIndexed { index, answer ->
                  val backgroundColor =
                      if (selectedAnswer == answer) AnswerSelectedColor else LightPurple
                  AnswerButton(
                      answer = answer,
                      backgroundColor = backgroundColor,
                      onClick = { viewModel.onAnswerSelected(answer) },
                      index = index)
                }
              }

              Spacer(modifier = Modifier.height(40.dp))

              NextButton(enabled = selectedAnswer != null, onClick = viewModel::goToNextQuestion)
            }
          } else {
            Text(
                text = "Your score: $score/${quizQuestions.size}",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("score_text"))

            Box(modifier = Modifier.height(550.dp).fillMaxWidth()) {
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
      ReturnToQuizSelectionButton(
          onClick = {
            viewModel.resetQuiz()
            navigationActions.navigateTo(Route.QUIZ)
          })
    }
  }
}
