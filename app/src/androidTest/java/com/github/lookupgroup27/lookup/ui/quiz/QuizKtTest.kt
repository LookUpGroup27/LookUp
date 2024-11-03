package com.github.lookupgroup27.lookup.ui.quiz

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.lookupgroup27.lookup.model.quiz.QuizViewModel
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

class QuizKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions: NavigationActions = mock()
  private val quizViewModel = QuizViewModel()

  fun setUp() {
    composeTestRule.setContent {
      QuizScreen(quizViewModel, navigationActions = mockNavigationActions)
    }
  }

  @Test
  fun quizScreen_displaysCorrectly() {
    setUp()
    composeTestRule.onNodeWithTag("quiz_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("go_back_button_quiz").assertIsDisplayed()
    composeTestRule.onNodeWithTag("quiz_background").assertIsDisplayed()

    // Check the title is correctly displayed
    composeTestRule.onNodeWithTag("quiz_title").assertIsDisplayed()
    composeTestRule.onNodeWithText("Take a Quiz").assertIsDisplayed()

    // Check the 2 themes are correctly displayed
    composeTestRule.onNodeWithTag("earth_button").assertIsDisplayed()
    composeTestRule.onNodeWithText("Earth").assertIsDisplayed()
    composeTestRule.onNodeWithTag("solar_system_button").assertIsDisplayed()
    composeTestRule.onNodeWithText("Solar System").assertIsDisplayed()
  }

  @Test
  fun quizScreen_clickBackButton_navigatesBack() {
    setUp()
    // Perform click on the back button
    composeTestRule.onNodeWithTag("go_back_button_quiz").performClick()

    // Verify navigation back action is triggered
    verify(mockNavigationActions).navigateTo(Screen.MENU)
  }

  @Test
  fun quizScreen_earthButtonRedirectCorrectly() {
    setUp()
    // Perform click on the Earth button
    composeTestRule.onNodeWithText("Earth").performClick()

    // Verify navigation to Earth quiz action is triggered
    verify(mockNavigationActions).navigateTo(Screen.QUIZ_PLAY)
  }

  @Test
  fun quizScreen_solarButtonRedirectCorrectly() {
    setUp()
    // Perform click on the Earth button
    composeTestRule.onNodeWithText("Solar System").performClick()

    // Verify navigation to Earth quiz action is triggered
    verify(mockNavigationActions).navigateTo(Screen.QUIZ_PLAY)
  }

  @Test
  fun quizOptionButton_behavesNormally() {
    composeTestRule.setContent {
      QuizOptionButton(
          text = "Earth",
          onClick = { mockNavigationActions.navigateTo(Screen.QUIZ_PLAY) },
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
      QuizOptionButton(text = "Solar System", onClick = {}, testTag = "solar_system_button")
    }

    composeTestRule.onNodeWithText("Solar System").assertIsDisplayed()
    composeTestRule.onNodeWithTag("solar_system_button").assertIsDisplayed()

    // Perform click on the Solar System button
    composeTestRule.onNodeWithTag("solar_system_button").performClick()

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
    composeTestRule.onNodeWithTag("solar_system_button").assertIsDisplayed()
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
