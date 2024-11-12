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

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
  val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

  private val _currentQuestionIndex = MutableStateFlow(0)
  val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

  private val _score = MutableStateFlow(0)
  val score: StateFlow<Int> = _score.asStateFlow()

  private val _showScore = MutableStateFlow(false)
  val showScore: StateFlow<Boolean> = _showScore.asStateFlow()

  private val _selectedAnswer = MutableStateFlow<String?>(null)
  val selectedAnswer: StateFlow<String?> = _selectedAnswer.asStateFlow()

  val quizTitle: String // Immutable, set once and not modified
    get() = repository.quizTitle

  fun loadQuizDataForTheme(theme: String, context: Context) {
    repository.loadQuizDataForTheme(theme, context)
    updateStateFlowFromRepository()
  }

  fun getQuestions(): List<QuizQuestion> = repository.quizQuestions

  fun getUserAnswers(): List<String> = repository.userAnswers.toList()

  fun getAllBestScores(): Map<String, Int> = repository.getAllBestScores()

  fun onAnswerSelected(answer: String) {
    repository.onAnswerSelected(answer)
    _selectedAnswer.value = repository.selectedAnswer
  }

  fun goToNextQuestion() {
    repository.goToNextQuestion()
    updateStateFlowFromRepository()
  }

  fun resetQuiz() {
    repository.resetQuiz()
    updateStateFlowFromRepository()
  }

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  fun updateStateFlowFromRepository() {
    _quizQuestions.value = repository.quizQuestions
    _currentQuestionIndex.value = repository.currentQuestionIndex
    _score.value = repository.score
    _showScore.value = repository.showScore
    _selectedAnswer.value = repository.selectedAnswer
  }

  companion object {
    fun provideFactory(context: Context): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return QuizViewModel(QuizRepository(context)) as T
          }
        }
  }
}
