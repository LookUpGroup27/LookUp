package com.github.lookupgroup27.lookup.ui.quiz

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class QuizScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions: NavigationActions = mock()

  @Test
  fun quizScreen_displaysQuizText() {
    composeTestRule.setContent { QuizScreen(navigationActions = mockNavigationActions) }

    // Verify that the "quiz_screen" text is displayed
    composeTestRule.onNodeWithTag("quiz_screen").assertIsDisplayed()
  }

  @Test
  fun quizScreen_clickBackButton_navigatesBack() {
    composeTestRule.setContent { QuizScreen(navigationActions = mockNavigationActions) }

    // Perform click on the back button
    composeTestRule.onNodeWithTag("go_back_button_quiz").performClick()

    // Verify navigation back action is triggered
    verify(mockNavigationActions).goBack()
  }
}
