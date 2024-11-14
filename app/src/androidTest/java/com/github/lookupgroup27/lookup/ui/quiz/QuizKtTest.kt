package com.github.lookupgroup27.lookup.ui.quiz

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.lookupgroup27.lookup.model.quiz.QuizRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

class QuizKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions: NavigationActions = mock()
  private val mockSharedPreferences: SharedPreferences = mock()
  private val mockEditor: SharedPreferences.Editor = mock()

  private lateinit var mockRepository: QuizRepository
  private lateinit var quizViewModel: QuizViewModel

  val mockContext: Context = mock()

  @Before
  fun setUp() {
    `when`(mockContext.getSharedPreferences(anyString(), anyInt()))
        .thenReturn(mockSharedPreferences)
    `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
    `when`(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor)
    `when`(mockEditor.apply()).then {}

    // Mock default behavior for getInt
    `when`(mockSharedPreferences.getInt(anyString(), anyInt())).thenAnswer { invocation ->
      invocation.getArgument(1) // Return the default value provided
    }
    mockRepository = QuizRepository(mockContext)
    quizViewModel = QuizViewModel(mockRepository)
  }

  @Test
  fun quizScreen_displaysCorrectly() {

    composeTestRule.setContent {
      QuizScreen(quizViewModel, navigationActions = mockNavigationActions)
    }

    composeTestRule.onNodeWithTag("quiz_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("go_back_button_quiz").assertIsDisplayed()
    composeTestRule.onNodeWithTag("quiz_background").assertIsDisplayed()

    // Check the title is correctly displayed
    composeTestRule.onNodeWithTag("quiz_title").assertIsDisplayed()
    composeTestRule.onNodeWithText("Take a Quiz").assertIsDisplayed()

    // Check the 2 themes are correctly displayed
    composeTestRule.onNodeWithTag("earth_button").assertIsDisplayed()
    composeTestRule.onNodeWithText("Earth").assertIsDisplayed()
    composeTestRule.onNodeWithTag("solar system_button").assertIsDisplayed()
    composeTestRule.onNodeWithText("Solar System").assertIsDisplayed()
  }

  @Test
  fun quizScreen_clickBackButton_navigatesBack() {
    composeTestRule.setContent {
      QuizScreen(quizViewModel, navigationActions = mockNavigationActions)
    }
    // Perform click on the back button
    composeTestRule.onNodeWithTag("go_back_button_quiz").performClick()

    // Verify navigation back action is triggered
    verify(mockNavigationActions).navigateTo(Screen.MENU)
  }

  @Test
  fun quizScreen_earthButtonRedirectCorrectly() {
    composeTestRule.setContent {
      QuizScreen(quizViewModel, navigationActions = mockNavigationActions)
    }
    // Perform click on the Earth button
    composeTestRule.onNodeWithText("Earth").performClick()

    // Verify navigation to Earth quiz action is triggered
    verify(mockNavigationActions).navigateTo(Screen.QUIZ_PLAY)
  }

  @Test
  fun quizScreen_solarButtonRedirectCorrectly() {
    composeTestRule.setContent {
      QuizScreen(quizViewModel, navigationActions = mockNavigationActions)
    }
    composeTestRule.onNodeWithText("Solar System").performClick()

    // Verify navigation to Earth quiz action is triggered
    verify(mockNavigationActions).navigateTo(Screen.QUIZ_PLAY)
  }

  @Test
  fun quizOptionButton_behavesNormally() {
    composeTestRule.setContent {
      QuizOptionButton(
          theme = "Earth",
          onClick = { mockNavigationActions.navigateTo(Screen.QUIZ_PLAY) },
          bestScore = "",
          testTag = "earth_button")
    }

    composeTestRule.onNodeWithText("Earth").assertIsDisplayed()
    composeTestRule.onNodeWithTag("earth_button").assertIsDisplayed()

    // Perform click on the Earth button
    composeTestRule.onNodeWithTag("earth_button").performClick()

    // Verify navigation to Earth quiz action is triggered
    verify(mockNavigationActions).navigateTo(Screen.QUIZ_PLAY)
  }

  @Test
  fun quizOptionButton_doesNothingOnClickEmpty() {
    composeTestRule.setContent {
      QuizOptionButton(
          theme = "Solar System", onClick = {}, bestScore = "", testTag = "solar system_button")
    }

    composeTestRule.onNodeWithText("Solar System").assertIsDisplayed()
    composeTestRule.onNodeWithTag("solar system_button").assertIsDisplayed()

    // Perform click on the Solar System button
    composeTestRule.onNodeWithTag("solar system_button").performClick()

    // Verify navigation to Solar System quiz action is triggered
    verifyNoInteractions(mockNavigationActions)
  }

  @Test
  fun quizScreen_displaysCorrectlyInLandscapeMode() {
    // Set device orientation to landscape
    setLandscapeOrientation()

    // Load the QuizScreen in landscape orientation
    composeTestRule.setContent {
      QuizScreen(quizViewModel, navigationActions = mockNavigationActions)
    }

    // Verify all key UI elements are displayed in landscape mode
    composeTestRule.onNodeWithTag("quiz_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("go_back_button_quiz").assertIsDisplayed()
    composeTestRule.onNodeWithTag("quiz_background").assertIsDisplayed()
    composeTestRule.onNodeWithTag("quiz_title").assertIsDisplayed()
    composeTestRule.onNodeWithText("Take a Quiz").assertIsDisplayed()

    // Verify theme buttons are displayed
    composeTestRule.onNodeWithTag("earth_button").assertIsDisplayed()
    composeTestRule.onNodeWithText("Earth").assertIsDisplayed()
    composeTestRule.onNodeWithTag("solar system_button").assertIsDisplayed()
    composeTestRule.onNodeWithText("Solar System").assertIsDisplayed()

    // Reset orientation to portrait after test
    resetOrientation()
  }

  private fun setLandscapeOrientation() {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    device.setOrientationLeft()
  }

  private fun resetOrientation() {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    device.setOrientationNatural()
  }
}
