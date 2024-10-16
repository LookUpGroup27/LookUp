package com.github.lookupgroup27.lookup.ui.quiz

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class QuizKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions: NavigationActions = mock()

  @Before
  fun setUp() {
    composeTestRule.setContent { QuizScreen(navigationActions = mockNavigationActions) }
  }

  @Test
  fun quizScreen_displaysCorrectly() {
    composeTestRule.onNodeWithTag("quiz_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("go_back_button_quiz").assertIsDisplayed()

    // Check the title is correctly displayed
    composeTestRule.onNodeWithText("Take a Quiz").assertIsDisplayed()

    // Check the 2 themes are correctly displayed
    composeTestRule.onNodeWithText("Earth").assertIsDisplayed()
    composeTestRule.onNodeWithText("Solar System").assertIsDisplayed()
  }

  @Test
  fun quizScreen_clickBackButton_navigatesBack() {
    // Perform click on the back button
    composeTestRule.onNodeWithTag("go_back_button_quiz").performClick()

    // Verify navigation back action is triggered
    verify(mockNavigationActions).goBack()
  }

  @Test
  fun quizScreen_earthButtonRedirectCorrectly() {
    // Perform click on the Earth button
    composeTestRule.onNodeWithText("Earth").performClick()

    // Verify navigation to Earth quiz action is triggered
    verify(mockNavigationActions).navigateTo(Screen.QUIZ_PLAY)
  }

  @Test
  fun quizScreen_solarButtonRedirectCorrectly() {
    // Perform click on the Earth button
    composeTestRule.onNodeWithText("Solar System").performClick()

    // Verify navigation to Earth quiz action is triggered
    verify(mockNavigationActions).navigateTo(Screen.QUIZ_PLAY)
  }
}
