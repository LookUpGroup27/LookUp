package com.github.lookupgroup27.lookup.ui.register

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavHostController
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.model.register.RegisterRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class MockRegisterRepository : RegisterRepository {
  override suspend fun registerUser(email: String, password: String) {}
}

class RegisterKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  private fun createMockViewModel(): RegisterViewModel {
    val repository = MockRegisterRepository()
    return RegisterViewModel(repository)
  }

  private fun mockNavigationActions() =
      NavigationActions(
          navController = NavHostController(ApplicationProvider.getApplicationContext()))

  @Test
  fun appLogo_isDisplayed() {
    composeTestRule.setContent {
      RegisterScreen(viewModel = createMockViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("app_logo").assertIsDisplayed()
  }

  @Test
  fun screenTitle_isDisplayed() {
    composeTestRule.setContent {
      RegisterScreen(viewModel = createMockViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("screen_title").assertIsDisplayed()
  }

  @Test
  fun emailField_isDisplayed() {
    composeTestRule.setContent {
      RegisterScreen(viewModel = createMockViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("email_field").assertIsDisplayed()
  }

  @Test
  fun passwordField_isDisplayed() {
    composeTestRule.setContent {
      RegisterScreen(viewModel = createMockViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("password_field").assertIsDisplayed()
  }

  @Test
  fun confirmPasswordField_isDisplayed() {
    composeTestRule.setContent {
      RegisterScreen(viewModel = createMockViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("confirm_password_field").assertIsDisplayed()
  }

  @Test
  fun registerButton_isDisplayed() {
    composeTestRule.setContent {
      RegisterScreen(viewModel = createMockViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("register_button").assertIsDisplayed()
  }

  @Test
  fun backButton_isDisplayed() {
    composeTestRule.setContent {
      RegisterScreen(viewModel = createMockViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("back_button").assertIsDisplayed()
  }
}
