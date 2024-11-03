import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.model.quiz.QuizQuestion
import com.github.lookupgroup27.lookup.model.quiz.QuizRepository
import com.github.lookupgroup27.lookup.model.quiz.QuizViewModel
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.quiz.QuizPlayScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

class QuizPlayKtTest {
  val mockNavigationActions: NavigationActions = mock()
  val mockContext: Context = mock()
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var quizViewModel: QuizViewModel
  private lateinit var testRepository: QuizRepository

  private val mockSharedPreferences: SharedPreferences = mock()
  private val mockEditor: SharedPreferences.Editor = mock()

  @Before
  fun setup() {
    // Set up the mock for SharedPreferences
    `when`(mockContext.getSharedPreferences(anyString(), anyInt()))
        .thenReturn(mockSharedPreferences)
    `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
    `when`(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor)
    `when`(mockEditor.apply()).then {}

    // Set up default return for getInt on SharedPreferences
    `when`(mockSharedPreferences.getInt(anyString(), anyInt())).thenAnswer { invocation ->
      invocation.getArgument(1) // Return the default value provided
    }

    testRepository =
        QuizRepository(mockContext).apply {
          quizQuestions =
              listOf(
                  QuizQuestion(
                      question = "In which galaxy is the solar system located?",
                      answers =
                          listOf(
                              "The Milky Way",
                              "The Great Spiral",
                              "The Magellanic Cloud",
                              "Andromeda"),
                      correctAnswer = "The Milky Way"))
          quizTitle = "Astronomy"
          currentQuestionIndex = 0
          score = 0
          showScore = false
          selectedAnswer = null
        }
    quizViewModel =
        QuizViewModel(testRepository).apply {
          _quizQuestions.postValue(
              listOf(
                  QuizQuestion(
                      question = "In which galaxy is the solar system located?",
                      answers =
                          listOf(
                              "The Milky Way",
                              "The Great Spiral",
                              "The Magellanic Cloud",
                              "Andromeda"),
                      correctAnswer = "The Milky Way")))
        }
  }

  @Test
  fun testQuizPlayScreenDisplaysQuestionAndAnswers() {
    // Launch the QuizPlayScreen
    composeTestRule.setContent {
      QuizPlayScreen(viewModel = quizViewModel, navigationActions = mockNavigationActions)
    }

    // Assert that the title is displayed
    composeTestRule.onNodeWithTag("quiz_title").assertTextEquals(" Quiz").assertIsDisplayed()

    // Assert that the question is displayed
    composeTestRule
        .onNodeWithTag("quiz_question")
        .assertTextEquals("In which galaxy is the solar system located?")
        .assertIsDisplayed()

    // Assert that all answer buttons are displayed
    composeTestRule
        .onNodeWithTag("answer_button_0")
        .assertTextEquals("The Milky Way")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("answer_button_1")
        .assertTextEquals("The Great Spiral")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("answer_button_2")
        .assertTextEquals("The Magellanic Cloud")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("answer_button_3")
        .assertTextEquals("Andromeda")
        .assertIsDisplayed()
  }

  @Test
  fun testAnswerSelectionEnablesNextButton() {
    // Launch the QuizPlayScreen
    composeTestRule.setContent {
      QuizPlayScreen(viewModel = quizViewModel, navigationActions = mockNavigationActions)
    }

    // Verify that the "Next" button is not displayed initially
    composeTestRule.onNodeWithTag("next_button").assertDoesNotExist()

    // Select an answer
    composeTestRule.onNodeWithTag("answer_button_0").performClick()

    // Wait for the UI to settle after the click
    composeTestRule.waitForIdle()

    // Verify that the "Next" button is now displayed and enabled
    composeTestRule
        .onNodeWithTag("next_button")
        .performScrollTo()
        .assertIsDisplayed()
        .assertIsEnabled()
  }

  @Test
  fun testNextButtonClickDisplaysScoreAndReturnButton() {
    // Launch the QuizPlayScreen
    composeTestRule.setContent {
      QuizPlayScreen(viewModel = quizViewModel, navigationActions = mockNavigationActions)
    }

    // Select an answer to enable the "Next" button
    composeTestRule.onNodeWithTag("answer_button_0").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    // Scroll to and click on the "Next" button
    composeTestRule.onNodeWithTag("next_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    // Scroll to and assert that the score text is displayed
    composeTestRule
        .onNodeWithTag("score_text")
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals("Your score: 1/1")

    // Scroll to and assert that the "Return to Quiz Selection" button is displayed
    composeTestRule
        .onNodeWithTag("return_to_quiz_selection_button")
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals("Return to Quiz Selection")
  }
}
