package com.github.lookupgroup27.lookup.ui.quiz

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.lookupgroup27.lookup.model.quiz.QuizQuestion
import com.github.lookupgroup27.lookup.model.quiz.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel responsible for managing the quiz state and business logic for the quiz feature. It
 * interacts with the [QuizRepository] to retrieve and update quiz data.
 *
 * @property repository The repository handling data storage and retrieval for quiz questions and
 *   user progress.
 */
class QuizViewModel(private val repository: QuizRepository) : ViewModel() {

  /** Flow of quiz questions for the current quiz theme. */
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
  val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

  /** Flow of the current question index in the quiz. */
  private val _currentQuestionIndex = MutableStateFlow(0)
  val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

  /** Flow of the user's current score. */
  private val _score = MutableStateFlow(0)
  val score: StateFlow<Int> = _score.asStateFlow()

  /** Flow indicating whether the score screen should be displayed. */
  private val _showScore = MutableStateFlow(false)
  val showScore: StateFlow<Boolean> = _showScore.asStateFlow()

  /** Flow representing the user's selected answer for the current question. */
  private val _selectedAnswer = MutableStateFlow<String?>(null)
  val selectedAnswer: StateFlow<String?> = _selectedAnswer.asStateFlow()

  /**
   * The title of the quiz, retrieved from the repository. This value is set once per quiz theme.
   */
  val quizTitle: String
    get() = repository.quizTitle

  /**
   * Loads quiz data for a given theme by calling the repository and updates state flows.
   *
   * @param theme The theme of the quiz to load.
   * @param context The context for accessing resources.
   */
  fun loadQuizDataForTheme(theme: String, context: Context) {
    repository.loadQuizDataForTheme(theme, context)
    updateStateFlowFromRepository()
  }

  /**
   * Retrieves the list of quiz questions from the repository.
   *
   * @return List of [QuizQuestion] representing all questions in the quiz.
   */
  fun getQuestions(): List<QuizQuestion> = repository.quizQuestions

  /**
   * Retrieves the list of user answers from the repository.
   *
   * @return List of Strings representing the user's answers.
   */
  fun getUserAnswers(): List<String> = repository.userAnswers.toList()

  /**
   * Retrieves a map of all themes with their best scores from the repository.
   *
   * @return Map where the key is the theme name and the value is the best score for that theme.
   */
  fun getAllBestScores(): Map<String, Int> = repository.getAllBestScores()

  /**
   * Handles the user's answer selection and updates the selected answer in the state flow.
   *
   * @param answer The answer chosen by the user.
   */
  fun onAnswerSelected(answer: String) {
    repository.onAnswerSelected(answer)
    _selectedAnswer.value = repository.selectedAnswer
  }

  /** Advances to the next question and updates the relevant state flows. */
  fun goToNextQuestion() {
    repository.goToNextQuestion()
    updateStateFlowFromRepository()
  }

  /** Resets the quiz state, including score and selected answers, by calling the repository. */
  fun resetQuiz() {
    repository.resetQuiz()
    updateStateFlowFromRepository()
  }

  /** Updates all state flows from the repository. Intended for testing and internal use. */
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  fun updateStateFlowFromRepository() {
    _quizQuestions.value = repository.quizQuestions
    _currentQuestionIndex.value = repository.currentQuestionIndex
    _score.value = repository.score
    _showScore.value = repository.showScore
    _selectedAnswer.value = repository.selectedAnswer
  }

  companion object {
    /**
     * Provides a factory for creating instances of [QuizViewModel], supplying the required
     * dependencies.
     *
     * @param context The context needed to initialize the repository.
     * @return A [ViewModelProvider.Factory] that can be used to instantiate [QuizViewModel].
     */
    fun provideFactory(context: Context): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return QuizViewModel(QuizRepository(context)) as T
          }
        }
  }
}
