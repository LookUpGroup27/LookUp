package com.github.lookupgroup27.lookup.model.quiz

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.InputStreamReader

open class QuizViewModel : ViewModel() {

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  val _quizQuestions = MutableLiveData<List<QuizQuestion>>()
  val quizQuestions: LiveData<List<QuizQuestion>> = _quizQuestions

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  var _currentQuestionIndex = MutableLiveData(0)
  val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) var _score = MutableLiveData(0)
  val score: LiveData<Int> = _score

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) var _showScore = MutableLiveData(false)
  val showScore: LiveData<Boolean> = _showScore

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  var _selectedAnswer = MutableLiveData<String?>()
  val selectedAnswer: LiveData<String?> = _selectedAnswer

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  val _quizTitle = MutableLiveData<String>()
  val quizTitle: LiveData<String> = _quizTitle

  private val quizFolder = "quizzes/"

  fun loadQuizDataForTheme(theme: String, context: Context) {
    val filePath = getFilePathForTheme(theme)
    val questions = loadCsvData(filePath, context)
    _quizQuestions.value = questions
    _currentQuestionIndex.value = 0
    _score.value = 0
    _showScore.value = false
    _quizTitle.value = theme
  }

  private fun getFilePathForTheme(theme: String): String {
    return when (theme) {
      "Solar System" -> "${quizFolder}solar_system_quiz.csv"
      "Earth" -> "${quizFolder}earth_quiz.csv"
      else -> "${quizFolder}default_quiz.csv"
    }
  }

  private fun loadCsvData(filePath: String, context: Context): List<QuizQuestion> {
    val questions = mutableListOf<QuizQuestion>()

    try {
      val inputStream = context.assets.open(filePath)
      val reader = InputStreamReader(inputStream)

      reader.useLines { lines ->
        lines.forEach { line ->
          val tokens = line.split(";")
          if (tokens.size == 5) {
            val questionText = tokens[0]
            val correctAnswer = tokens[1]
            val wrongAnswers = listOf(tokens[2], tokens[3], tokens[4])
            val shuffledAnswers = (wrongAnswers + correctAnswer).shuffled()
            questions.add(QuizQuestion(questionText, shuffledAnswers, correctAnswer))
          }
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

    return questions
  }

  fun onAnswerSelected(answer: String) {
    _selectedAnswer.value = answer
  }

  fun goToNextQuestion() {
    if (_selectedAnswer.value ==
        _quizQuestions.value?.get(_currentQuestionIndex.value ?: 0)?.correctAnswer) {
      _score.value = _score.value?.plus(1)
    }

    if ((_currentQuestionIndex.value ?: 0) == (_quizQuestions.value?.size ?: 0) - 1) {
      _showScore.value = true
    } else {
      _currentQuestionIndex.value = (_currentQuestionIndex.value ?: 0) + 1
      _selectedAnswer.value = null
    }
  }

  fun resetQuiz() {
    _currentQuestionIndex.value = 0
    _score.value = 0
    _showScore.value = false
    _selectedAnswer.value = null
    _quizQuestions.value = emptyList()
  }
}
