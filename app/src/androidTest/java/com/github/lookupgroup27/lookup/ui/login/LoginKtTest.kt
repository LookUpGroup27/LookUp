package com.github.lookupgroup27.lookup.ui.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavHostController
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.model.login.LoginRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class MockLoginRepository : LoginRepository {
  override suspend fun loginUser(email: String, password: String) {}
}

class LoginKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  private fun createMockViewModel(): LoginViewModel {
    val repository = MockLoginRepository()
    return LoginViewModel(repository)
  }

  private fun mockNavigationActions() =
      NavigationActions(
          navController = NavHostController(ApplicationProvider.getApplicationContext()))

  @Test
  fun appLogo_isDisplayed() {
    composeTestRule.setContent {
      LoginScreen(viewModel = createMockViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("app_logo").assertIsDisplayed()
  }

  @Test
  fun screenTitle_isDisplayed() {
    composeTestRule.setContent {
      LoginScreen(viewModel = createMockViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("screen_title").assertIsDisplayed()
  }

  @Test
  fun emailField_isDisplayed() {
    composeTestRule.setContent {
      LoginScreen(viewModel = createMockViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("email_field").assertIsDisplayed()
  }

  @Test
  fun passwordField_isDisplayed() {
    composeTestRule.setContent {
      LoginScreen(viewModel = createMockViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("password_field").assertIsDisplayed()
  }

  @Test
  fun loginButton_isDisplayed() {
    composeTestRule.setContent {
      LoginScreen(viewModel = createMockViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("login_button").assertIsDisplayed()
  }

  @Test
  fun backButton_isDisplayed() {
    composeTestRule.setContent {
      LoginScreen(viewModel = createMockViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("back_button").assertIsDisplayed()
  }

  @Test
  fun forgotPasswordButton_isDisplayed() {
    composeTestRule.setContent {
      LoginScreen(viewModel = createMockViewModel(), navigationActions = mockNavigationActions())
    }
    composeTestRule.onNodeWithTag("forgot_password_button").assertIsDisplayed()
  }
}
