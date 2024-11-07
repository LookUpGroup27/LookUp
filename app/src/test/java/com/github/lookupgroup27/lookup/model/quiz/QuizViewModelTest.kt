package com.github.lookupgroup27.lookup.model.quiz

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import java.io.ByteArrayInputStream
import java.io.InputStream
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

class QuizViewModelTest {

  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

  private lateinit var quizViewModel: QuizViewModel
  private lateinit var mockContext: Context
  private lateinit var testRepository: QuizRepository

  @Before
  fun setUp() {
    // Initialize the repository and ViewModel
    mockContext = mock()

    // Mock SharedPreferences and its Editor
    val mockSharedPreferences: SharedPreferences = mock()
    val mockEditor: SharedPreferences.Editor = mock()

    // Set up SharedPreferences to return the mockEditor when edit() is called
    `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)

    // Set up the editor to return itself when putInt() or apply() is called
    `when`(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor)
    `when`(mockEditor.apply()).then {}

    // Return the mock SharedPreferences when getSharedPreferences is called
    `when`(mockContext.getSharedPreferences(anyString(), anyInt()))
        .thenReturn(mockSharedPreferences)

    // Initialize the QuizRepository with the mocked context
    testRepository = QuizRepository(mockContext)
    quizViewModel = QuizViewModel(testRepository)
  }

  @Test
  fun `loadQuizDataForTheme loads Solar System quiz correctly`() {
    // Simulate reading from assets/solar_system_quiz.csv
    val solarSystemQuizCsv = "What is the largest planet?;Jupiter;Saturn;Mars;Venus\n"
    mockAssetFile("quizzes/solar_system_quiz.csv", solarSystemQuizCsv)

    // Load the quiz data for the "Solar System" theme
    quizViewModel.loadQuizDataForTheme("Solar System", mockContext)

    // Assert the correct quiz data is loaded
    val quizQuestions = quizViewModel.quizQuestions.value
    assertNotNull(quizQuestions)
    assertEquals(1, quizQuestions.size)
    assertEquals("What is the largest planet?", quizQuestions.get(0)?.question)
    assertTrue(quizQuestions.get(0)?.answers?.contains("Jupiter") == true)
    assertTrue(quizQuestions.get(0)?.answers?.contains("Saturn") == true)
    assertTrue(quizQuestions.get(0)?.answers?.contains("Mars") == true)
    assertTrue(quizQuestions.get(0)?.answers?.contains("Venus") == true)
    assertEquals("Jupiter", quizQuestions.get(0)?.correctAnswer)
  }

  @Test
  fun `loadQuizDataForTheme loads a single Earth quiz question correctly()`() {
    // Simulate reading a single question from the assets/earth_quiz.csv
    val earthQuizCsv =
        "In which galaxy is the solar system located?;The Milky Way;Andromeda;The Great Spiral;The Magellanic Cloud\n"
    mockAssetFile("quizzes/earth_quiz.csv", earthQuizCsv)

    // Load the quiz data for the "Earth" theme
    quizViewModel.loadQuizDataForTheme("Earth", mockContext)

    // Assert that the correct question was loaded
    val quizQuestions = quizViewModel.quizQuestions.value
    assertNotNull(quizQuestions)
    assertEquals(1, quizQuestions.size)
    assertEquals("In which galaxy is the solar system located?", quizQuestions.get(0)?.question)
    assertTrue(quizQuestions.get(0)?.answers?.contains("The Milky Way") == true)
    assertTrue(quizQuestions.get(0)?.answers?.contains("Andromeda") == true)
    assertTrue(quizQuestions.get(0)?.answers?.contains("The Great Spiral") == true)
    assertTrue(quizQuestions.get(0)?.answers?.contains("The Magellanic Cloud") == true)
    assertEquals("The Milky Way", quizQuestions.get(0)?.correctAnswer)
  }

  // Test onAnswerSelected
  @Test
  fun `onAnswerSelected stores selected answer`() {
    // Select an answer
    quizViewModel.onAnswerSelected("Jupiter")

    // Verify the selected answer is stored correctly
    assertEquals("Jupiter", quizViewModel.selectedAnswer.value)
  }

  // Test goToNextQuestion with correct answer
  @Test
  fun `goToNextQuestion updates score and proceeds to next question`() {
    // Load Solar System quiz data
    val solarSystemQuizCsv =
        "What is the largest planet?;Jupiter;Saturn;Mars;Venus\nWhat?;test1;test2;test3;test4\n"
    mockAssetFile("quizzes/solar_system_quiz.csv", solarSystemQuizCsv)
    quizViewModel.loadQuizDataForTheme("Solar System", mockContext)

    // Select the correct answer and go to the next question
    quizViewModel.onAnswerSelected("Jupiter")
    quizViewModel.goToNextQuestion()

    // Verify the score is updated and other states are reset
    assertEquals(1, quizViewModel.score.value)
    assertEquals(1, quizViewModel.currentQuestionIndex.value) // Moves to the next question index
    assertNull(quizViewModel.selectedAnswer.value) // Selected answer is reset
  }

  // Test goToNextQuestion with correct answer
  @Test
  fun `goToNextQuestion doesn't update score when bad answer selected`() {
    // Load Solar System quiz data
    val solarSystemQuizCsv =
        "What is the largest planet?;Jupiter;Saturn;Mars;Venus\nWhat?;test1;test2;test3;test4\n"
    mockAssetFile("quizzes/solar_system_quiz.csv", solarSystemQuizCsv)
    quizViewModel.loadQuizDataForTheme("Solar System", mockContext)

    // Select the correct answer and go to the next question
    quizViewModel.onAnswerSelected("Mars")
    quizViewModel.goToNextQuestion()

    // Verify the score is updated and other states are reset
    assertEquals(0, quizViewModel.score.value)
    assertEquals(1, quizViewModel.currentQuestionIndex.value) // Moves to the next question index
    assertNull(quizViewModel.selectedAnswer.value) // Selected answer is reset
  }

  // Test resetQuiz
  @Test
  fun `resetQuiz resets quiz state`() {
    // Load quiz data and simulate an answer selection
    val solarSystemQuizCsv = "What is the largest planet?;Jupiter;Saturn;Mars;Venus\n"
    mockAssetFile("quizzes/solar_system_quiz.csv", solarSystemQuizCsv)
    quizViewModel.loadQuizDataForTheme("Solar System", mockContext)

    quizViewModel.onAnswerSelected("Jupiter")
    quizViewModel.goToNextQuestion()

    // Reset the quiz and verify all states are reset
    quizViewModel.resetQuiz()
    assertEquals(0, quizViewModel.currentQuestionIndex.value)
    assertEquals(0, quizViewModel.score.value)
    assertFalse(quizViewModel.showScore.value)
    assertNull(quizViewModel.selectedAnswer.value)
    assertTrue(quizViewModel.quizQuestions.value.isEmpty() == true)
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
