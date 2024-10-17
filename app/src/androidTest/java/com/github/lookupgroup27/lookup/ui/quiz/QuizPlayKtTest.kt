import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.model.quiz.QuizQuestion
import com.github.lookupgroup27.lookup.model.quiz.QuizViewModel
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.quiz.QuizPlayScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class QuizPlayKtTest {
  val mockNavigationActions: NavigationActions = mock()
  @get:Rule val TcomposeTestRule = createComposeRule()

  private lateinit var quizViewModel: QuizViewModel

  @Before
  fun setup() {
    // Initialize the ViewModel with test data
    quizViewModel =
        QuizViewModel().apply {
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

    // Verify that the "Next" button is now displayed and enabled
    composeTestRule.onNodeWithTag("next_button").assertIsDisplayed().assertIsEnabled()
  }

  @Test
  fun testNextButtonClickDisplaysScoreAndReturnButton() {
    // Launch the QuizPlayScreen
    composeTestRule.setContent {
      QuizPlayScreen(viewModel = quizViewModel, navigationActions = mockNavigationActions)
    }

    // Select an answer to enable the "Next" button
    composeTestRule.onNodeWithTag("answer_button_0").performClick()

    // Perform click on "Next" button
    composeTestRule.onNodeWithTag("next_button").performClick()

    // Assert that the score text is displayed
    composeTestRule
        .onNodeWithTag("score_text")
        .assertIsDisplayed()
        .assertTextEquals("Your score: 1/1")

    // Assert that the "Return to Quiz Selection" button is displayed
    composeTestRule
        .onNodeWithTag("return_to_quiz_selection_button")
        .assertIsDisplayed()
        .assertTextEquals("Return to Quiz Selection")
  }
}
