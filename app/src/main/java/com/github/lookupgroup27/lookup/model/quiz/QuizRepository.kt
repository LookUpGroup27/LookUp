package com.github.lookupgroup27.lookup.model.quiz

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import java.io.InputStreamReader

/**
 * Repository responsible for managing quiz data, including questions, user progress, and scores.
 *
 * @param context The application context used for accessing assets and shared preferences.
 */
class QuizRepository(private val context: Context) {

  private val quizFolder = "quizzes/"

  /** List of quiz questions for the current quiz theme. */
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  var quizQuestions: List<QuizQuestion> = emptyList()

  /** The index of the current question being displayed. */
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) var currentQuestionIndex = 0

  /** The user's current score in the quiz. */
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) var score = 0

  /** Flag indicating whether the score screen should be displayed. */
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) var showScore = false

  /** The answer selected by the user for the current question. */
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) var selectedAnswer: String? = null

  /** The title of the current quiz theme. */
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) var quizTitle: String = ""

  /** List of answers provided by the user during the quiz. */
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  var userAnswers: MutableList<String> = mutableListOf()

  private val preferences: SharedPreferences =
      context.getSharedPreferences("quiz_scores", Context.MODE_PRIVATE)

  /**
   * Retrieves the best score for a specific quiz theme.
   *
   * @param theme The name of the quiz theme.
   * @return The highest score achieved by the user for the specified theme.
   */
  fun getBestScore(theme: String): Int {
    return preferences.getInt(theme, 0)
  }

  /**
   * Sets a new best score for a specific theme if the current score is higher.
   *
   * @param theme The name of the quiz theme.
   * @param score The score to update as the best score.
   */
  private fun setBestScore(theme: String, score: Int) {
    val currentBestScore = getBestScore(theme)
    if (score > currentBestScore) {
      preferences.edit().putInt(theme, score).apply()
    }
  }

  /**
   * Retrieves a map of all themes and their respective best scores.
   *
   * @return A map where the key is the theme name and the value is the best score.
   */
  fun getAllBestScores(): Map<String, Int> {
    val scoresMap = mutableMapOf<String, Int>()
    preferences.all.forEach { (key, value) -> if (value is Int) scoresMap[key] = value }

    // Ensure default themes are included with a default score of 0 if not already present.
    listOf(
            "Earth",
            "Solar System",
            "Space Exploration",
            "Cosmic Objects",
            "Astronomical Tools",
            "Astronomers and Scientists")
        .forEach { theme -> scoresMap.putIfAbsent(theme, 0) }

    return scoresMap
  }

  /**
   * Loads quiz data for the specified theme by reading from an asset file.
   *
   * @param theme The theme of the quiz to load.
   * @param context The context used to access assets.
   */
  fun loadQuizDataForTheme(theme: String, context: Context) {
    val filePath = getFilePathForTheme(theme)
    quizQuestions = loadCsvData(filePath, context)
    currentQuestionIndex = 0
    score = 0
    showScore = false
    quizTitle = theme
  }

  /**
   * Determines the file path for the CSV file corresponding to the given theme.
   *
   * @param theme The name of the quiz theme.
   * @return The file path of the CSV file for the theme.
   */
  private fun getFilePathForTheme(theme: String): String {
    return when (theme) {
      "Solar System" -> "${quizFolder}solar_system_quiz.csv"
      "Earth" -> "${quizFolder}earth_quiz.csv"
      "Space Exploration" -> "${quizFolder}space_exploration_quiz.csv"
      "Cosmic Objects" -> "${quizFolder}cosmic_objects_quiz.csv"
      "Astronomical Tools" -> "${quizFolder}astronomical_tools_quiz.csv"
      "Astronomers and Scientists" -> "${quizFolder}astronomers_scientists_quiz.csv"
      else -> "${quizFolder}default_quiz.csv"
    }
  }

  /**
   * Reads quiz questions from a CSV file and parses them into a list of [QuizQuestion].
   *
   * @param filePath The path to the CSV file.
   * @param context The context used to access assets.
   * @return A list of [QuizQuestion] parsed from the CSV file.
   */
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

  /**
   * Records the user's selected answer for the current question.
   *
   * @param answer The answer selected by the user.
   */
  fun onAnswerSelected(answer: String) {
    selectedAnswer = answer
  }

  /**
   * Advances to the next question in the quiz or concludes the quiz if it is the last question.
   * Updates the score and determines whether to show the score screen.
   */
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

  /** Resets the quiz state, clearing questions, scores, and user answers. */
  fun resetQuiz() {
    currentQuestionIndex = 0
    score = 0
    showScore = false
    selectedAnswer = null
    quizQuestions = emptyList()
    userAnswers.clear()
  }
}
