package com.github.lookupgroup27.lookup.model.quiz

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import java.io.InputStreamReader

class QuizRepository(private val context: Context) {

  private val quizFolder = "quizzes/"

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  var quizQuestions: List<QuizQuestion> = emptyList()

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) var currentQuestionIndex = 0

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) var score = 0

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) var showScore = false

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) var selectedAnswer: String? = null

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) var quizTitle: String = ""

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  var userAnswers: MutableList<String> = mutableListOf()

  private val preferences: SharedPreferences =
      context.getSharedPreferences("quiz_scores", Context.MODE_PRIVATE)

  fun getBestScore(theme: String): Int {
    return preferences.getInt(theme, 0)
  }

  // Updates the best score if the new score is higher
  fun setBestScore(theme: String, score: Int) {
    val currentBestScore = getBestScore(theme)
    if (score > currentBestScore) {
      preferences.edit().putInt(theme, score).apply() // Save the new best score
    }
  }

  // Retrieves all scores as a map of theme to score, defaulting missing scores to 0
  fun getAllBestScores(): Map<String, Int> {
    val scoresMap = mutableMapOf<String, Int>()
    preferences.all.forEach { (key, value) -> if (value is Int) scoresMap[key] = value }
    // Optionally add default scores for known themes if not present
    listOf("Earth", "Solar System").forEach { theme -> scoresMap.putIfAbsent(theme, 0) }
    return scoresMap
  }

  fun loadQuizDataForTheme(theme: String, context: Context) {
    val filePath = getFilePathForTheme(theme)
    quizQuestions = loadCsvData(filePath, context)
    currentQuestionIndex = 0
    score = 0
    showScore = false
    quizTitle = theme
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
    selectedAnswer = answer
  }

  fun goToNextQuestion() {

    if (selectedAnswer == quizQuestions.getOrNull(currentQuestionIndex)?.correctAnswer) {
      score++
    }
    selectedAnswer?.let { userAnswers.add(it) }
    if (currentQuestionIndex == quizQuestions.size - 1) {
      showScore = true
      if (getBestScore(quizTitle) < score) {
        setBestScore(quizTitle, score)
      }
    } else {
      currentQuestionIndex++
      selectedAnswer = null
    }
  }

  fun resetQuiz() {
    currentQuestionIndex = 0
    score = 0
    showScore = false
    selectedAnswer = null
    quizQuestions = emptyList()
    userAnswers.clear()
  }
}
