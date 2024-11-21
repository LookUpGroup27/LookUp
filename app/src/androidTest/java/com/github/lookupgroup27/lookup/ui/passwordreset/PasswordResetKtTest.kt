package com.github.lookupgroup27.lookup.ui.passwordreset

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.model.passwordreset.PasswordResetRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class MockPasswordResetRepository : PasswordResetRepository {
  override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {

    return if (email == "success@example.com") {
      Result.success(Unit)
    } else {

      Result.failure(Exception("Mock error for testing"))
    }
  }
}

class PasswordResetScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private fun createRealViewModel(): PasswordResetViewModel {
    val repository = MockPasswordResetRepository()
    return PasswordResetViewModel(repository)
  }

  private fun mockNavigationActions() =
      NavigationActions(
          navController = NavHostController(ApplicationProvider.getApplicationContext()))

  @Test
  fun appLogo_isDisplayed() {
    composeTestRule.setContent {
      PasswordResetScreen(
          viewModel = createRealViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("app_logo").assertIsDisplayed()
  }

  @Test
  fun screenTitle_isDisplayed() {
    composeTestRule.setContent {
      PasswordResetScreen(
          viewModel = createRealViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("screen_title").assertIsDisplayed()
  }

  @Test
  fun emailField_isDisplayed() {
    composeTestRule.setContent {
      PasswordResetScreen(
          viewModel = createRealViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("email_field").assertIsDisplayed()
  }

  @Test
  fun resetButton_isDisplayed() {
    composeTestRule.setContent {
      PasswordResetScreen(
          viewModel = createRealViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("reset_button").assertIsDisplayed()
  }

  @Test
  fun backButton_isDisplayed() {
    composeTestRule.setContent {
      PasswordResetScreen(
          viewModel = createRealViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("back_button").assertIsDisplayed()
  }

  @Test
  fun emailField_acceptsInput() {
    composeTestRule.setContent {
      PasswordResetScreen(
          viewModel = createRealViewModel(), navigationActions = mockNavigationActions())
    }

    composeTestRule.onNodeWithTag("email_field").performTextInput("test@example.com")
  }

  @Test
  fun resetButton_triggersResetPassword() {
    composeTestRule.setContent {
      PasswordResetScreen(
          viewModel = createRealViewModel(), navigationActions = mockNavigationActions())
    }

    composeTestRule.onNodeWithTag("email_field").performTextInput("success@example.com")
    composeTestRule.onNodeWithTag("reset_button").performClick()
  }
}
