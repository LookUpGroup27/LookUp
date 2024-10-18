package com.github.lookupgroup27.lookup.model.quiz

import android.content.Context
import android.content.res.AssetManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import java.io.ByteArrayInputStream
import java.io.InputStream
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

class QuizViewModelTest {

  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

  private lateinit var quizViewModel: QuizViewModel
  private lateinit var mockContext: Context

  @Before
  fun setUp() {
    quizViewModel = QuizViewModel()
    mockContext = mock()
  }

  @Test
  fun `loadQuizDataForTheme loads a single Earth quiz question correctly`() {
    // Simulate reading a single question from the assets/earth_quiz.csv
    val earthQuizCsv =
        "In which galaxy is the solar system located?;The Milky Way;Andromeda;The Great Spiral;The Magellanic Cloud\n"
    mockAssetFile("quizzes/earth_quiz.csv", earthQuizCsv)

    // Load the quiz data for the "Earth" theme
    quizViewModel.loadQuizDataForTheme("Earth", mockContext)

    // Assert that the correct question was loaded
    val quizQuestions = quizViewModel.quizQuestions.value
    assertNotNull(quizQuestions)
    assertEquals(1, quizQuestions?.size)
    assertEquals("In which galaxy is the solar system located?", quizQuestions?.get(0)?.question)
    assertTrue(quizQuestions?.get(0)?.answers?.contains("The Milky Way") == true)
    assertTrue(quizQuestions?.get(0)?.answers?.contains("Andromeda") == true)
    assertTrue(quizQuestions?.get(0)?.answers?.contains("The Great Spiral") == true)
    assertTrue(quizQuestions?.get(0)?.answers?.contains("The Magellanic Cloud") == true)
    assertEquals("The Milky Way", quizQuestions?.get(0)?.correctAnswer)
  }
  // Test loading Solar System quiz data
  @Test
  fun `loadQuizDataForTheme loads Solar System quiz correctly`() {
    // Simulate reading from the assets/solar_system_quiz.csv
    val solarSystemQuizCsv = "What is the largest planet?;Jupiter;Saturn;Mars;Venus\n"
    mockAssetFile("quizzes/solar_system_quiz.csv", solarSystemQuizCsv)

    // Load the quiz data for the "Solar System" theme
    quizViewModel.loadQuizDataForTheme("Solar System", mockContext)

    // Assert the correct quiz data is loaded
    val quizQuestions = quizViewModel.quizQuestions.value
    assertNotNull(quizQuestions)
    assertEquals(1, quizQuestions?.size)
    assertEquals("What is the largest planet?", quizQuestions?.get(0)?.question)
    assertTrue(quizQuestions?.get(0)?.answers?.contains("Jupiter") == true)
    assertTrue(quizQuestions?.get(0)?.answers?.contains("Saturn") == true)
    assertTrue(quizQuestions?.get(0)?.answers?.contains("Mars") == true)
    assertTrue(quizQuestions?.get(0)?.answers?.contains("Venus") == true)
    assertEquals("Jupiter", quizQuestions?.get(0)?.correctAnswer)
  }

  // Test onAnswerSelected
  @Test
  fun `onAnswerSelected stores selected answer`() {
    // Select an answer
    quizViewModel.onAnswerSelected("Asia")

    // Verify the selected answer is stored correctly
    assertEquals("Asia", quizViewModel.selectedAnswer.value)
  }

  // Test goToNextQuestion with correct answer
  @Test
  fun `goToNextQuestion updates score and proceeds to next question`() {
    // Simulate quiz data
    val quizQuestions =
        listOf(
            QuizQuestion(
                "What is the largest continent?",
                listOf("Asia", "Europe", "Africa", "Antarctica"),
                "Asia"),
            QuizQuestion(
                "What is the smallest continent?",
                listOf("Australia", "Europe", "Africa", "Antarctica"),
                "Australia"))
    quizViewModel._quizQuestions.postValue(quizQuestions)
    quizViewModel.onAnswerSelected("Asia")

    // Go to the next question
    quizViewModel.goToNextQuestion()

    // Verify the score is updated
    assertEquals(1, quizViewModel.score.value)
    // Verify the question index is updated
    assertEquals(1, quizViewModel.currentQuestionIndex.value)
    // Verify the selected answer is reset
    assertNull(quizViewModel.selectedAnswer.value)
  }

  // Test goToNextQuestion with incorrect answer
  @Test
  fun `goToNextQuestion does not update score for incorrect answer`() {
    // Simulate quiz data
    val quizQuestions =
        listOf(
            QuizQuestion(
                "What is the largest continent?",
                listOf("Asia", "Europe", "Africa", "Antarctica"),
                "Asia"),
            QuizQuestion(
                "What is the smallest continent?",
                listOf("Australia", "Europe", "Africa", "Antarctica"),
                "Australia"))
    quizViewModel._quizQuestions.postValue(quizQuestions)
    quizViewModel.onAnswerSelected("Europe")

    // Go to the next question
    quizViewModel.goToNextQuestion()

    // Verify the score is not updated
    assertEquals(0, quizViewModel.score.value)
    // Verify the question index is updated
    assertEquals(1, quizViewModel.currentQuestionIndex.value)
  }

  // Test resetQuiz
  @Test
  fun `resetQuiz resets quiz state`() {
    // Simulate quiz data and interactions
    val quizQuestions =
        listOf(
            QuizQuestion(
                "What is the largest continent?",
                listOf("Asia", "Europe", "Africa", "Antarctica"),
                "Asia"))
    quizViewModel._quizQuestions.postValue(quizQuestions)
    quizViewModel.onAnswerSelected("Asia")
    quizViewModel.goToNextQuestion()

    // Reset the quiz
    quizViewModel.resetQuiz()

    // Verify that the state is reset
    assertEquals(0, quizViewModel.currentQuestionIndex.value)
    assertEquals(0, quizViewModel.score.value)
    assertFalse(quizViewModel.showScore.value ?: true)
    assertNull(quizViewModel.selectedAnswer.value)
    assertTrue(quizViewModel.quizQuestions.value?.isEmpty() == true)
  }

  // Helper method to mock the asset file reading
  private fun mockAssetFile(filePath: String, fileContent: String) {
    // Mock the AssetManager
    val mockAssetManager: AssetManager = mock()

    // Create an InputStream from the file content
    val inputStream: InputStream = ByteArrayInputStream(fileContent.toByteArray())

    // When the mockContext.getAssets() is called, return the mockAssetManager
    `when`(mockContext.assets).thenReturn(mockAssetManager)

    // When mockAssetManager.open(filePath) is called, return the inputStream
    `when`(mockAssetManager.open(filePath)).thenReturn(inputStream)
  }
}
