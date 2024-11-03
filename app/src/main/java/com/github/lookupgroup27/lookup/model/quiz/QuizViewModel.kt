package com.github.lookupgroup27.lookup.model.quiz

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  val _quizQuestions = MutableLiveData<List<QuizQuestion>>()
  val quizQuestions: LiveData<List<QuizQuestion>> = _quizQuestions

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  val _currentQuestionIndex = MutableLiveData(0)
  val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) val _score = MutableLiveData(0)
  val score: LiveData<Int> = _score

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) val _showScore = MutableLiveData(false)
  val showScore: LiveData<Boolean> = _showScore

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  val _selectedAnswer = MutableLiveData<String?>()
  val selectedAnswer: LiveData<String?> = _selectedAnswer

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  val _quizTitle = MutableLiveData<String>()
  val quizTitle: LiveData<String> = _quizTitle

  fun loadQuizDataForTheme(theme: String, context: Context) {
    repository.loadQuizDataForTheme(theme, context)
    updateLiveDataFromRepository()
  }

  fun getQuestions(): List<QuizQuestion> {
    return repository.quizQuestions
  }

  fun getUserAnswers(): List<String> {
    return repository.userAnswers.toList()
  }

  fun getBestScore(theme: String): Int {
    return repository.getBestScore(theme)
  }

  fun getAllBestScores(): Map<String, Int> {
    return repository.getAllBestScores()
  }

  fun onAnswerSelected(answer: String) {
    repository.onAnswerSelected(answer)
    _selectedAnswer.value = repository.selectedAnswer
  }

  fun goToNextQuestion() {
    repository.goToNextQuestion()
    updateLiveDataFromRepository()
  }

  fun resetQuiz() {
    repository.resetQuiz()
    updateLiveDataFromRepository()
  }

  private fun updateLiveDataFromRepository() {
    _quizQuestions.value = repository.quizQuestions
    _currentQuestionIndex.value = repository.currentQuestionIndex
    _score.value = repository.score
    _showScore.value = repository.showScore
    _selectedAnswer.value = repository.selectedAnswer
    _quizTitle.value = repository.quizTitle
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
