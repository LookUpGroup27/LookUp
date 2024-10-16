package com.github.lookupgroup27.lookup.ui.quiz

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class QuizPlayKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions: NavigationActions = mock()

  @Before
  fun setUp() {
    composeTestRule.setContent { QuizPlayScreen(navigationActions = mockNavigationActions) }
  }

  @Test
  fun quizPlayScreen_displaysCorrectly() {
    composeTestRule.onNodeWithText("Quiz Play Screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("go_back_button_quiz").assertIsDisplayed()
  }

  @Test
  fun quizPlayScreen_clickBackButton_navigatesBack() {
    composeTestRule.onNodeWithTag("go_back_button_quiz").performClick()
    verify(mockNavigationActions).goBack()
  }
}
