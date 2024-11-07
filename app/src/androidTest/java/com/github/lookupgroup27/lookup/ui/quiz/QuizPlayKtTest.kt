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
    `when`(mockContext.getSharedPreferences(anyString(), anyInt()))
        .thenReturn(mockSharedPreferences)
    `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
    `when`(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor)
    `when`(mockEditor.apply()).then {}

    `when`(mockSharedPreferences.getInt(anyString(), anyInt())).thenAnswer { invocation ->
      invocation.getArgument(1)
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
    quizViewModel = QuizViewModel(testRepository)
    quizViewModel.updateStateFlowFromRepository()
  }

  @Test
  fun testQuizPlayScreenDisplaysQuestionAndAnswers() {

    composeTestRule.setContent {
      QuizPlayScreen(viewModel = quizViewModel, navigationActions = mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag("quiz_title")
        .assertTextEquals("Astronomy Quiz")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("quiz_question")
        .assertTextEquals("In which galaxy is the solar system located?")
        .assertIsDisplayed()

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
    composeTestRule.setContent {
      QuizPlayScreen(viewModel = quizViewModel, navigationActions = mockNavigationActions)
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("next_button").assertIsNotEnabled()
    composeTestRule.onNodeWithTag("answer_button_0").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("next_button").assertIsDisplayed().assertIsEnabled()
  }

  @Test
  fun testNextButtonClickDisplaysScoreAndReturnButton() {

    composeTestRule.setContent {
      QuizPlayScreen(viewModel = quizViewModel, navigationActions = mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("answer_button_0").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("next_button").performClick()

    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("score_text")
        .assertIsDisplayed()
        .assertTextEquals("Your score: 1/1")
    composeTestRule
        .onNodeWithTag("return_to_quiz_selection_button")
        .assertIsDisplayed()
        .assertTextEquals("Return to Quiz Selection")
  }
}
