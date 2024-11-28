package com.github.lookupgroup27.lookup.ui.login

import com.github.lookupgroup27.lookup.model.login.LoginRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

  private lateinit var viewModel: LoginViewModel
  private lateinit var mockRepository: LoginRepository

  @Before
  fun setUp() {
    mockRepository = mock()
    viewModel = LoginViewModel(mockRepository)
  }

  @Test
  fun `onEmailChanged updates email in uiState`() = runTest {
    viewModel.onEmailChanged("test@example.com")

    val currentState = viewModel.uiState.first()
    assertEquals("test@example.com", currentState.email)
  }

  @Test
  fun `onPasswordChanged updates password in uiState`() = runTest {
    viewModel.onPasswordChanged("password123")

    val currentState = viewModel.uiState.first()
    assertEquals("password123", currentState.password)
  }

  @Test
  fun `clearFields resets email and password in uiState`() = runTest {
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("password123")

    viewModel.clearFields()

    val currentState = viewModel.uiState.first()
    assertEquals("", currentState.email)
    assertEquals("", currentState.password)
  }

  @Test
  fun `loginUser triggers onError when email is blank`() = runTest {
    viewModel.onEmailChanged("")
    viewModel.onPasswordChanged("password123")

    var successCalled = false
    var errorCalled = false

    viewModel.loginUser(onSuccess = { successCalled = true }, onError = { errorCalled = true })

    assertFalse(successCalled)
    assertTrue(errorCalled)
  }

  @Test
  fun `loginUser triggers onError when password is blank`() = runTest {
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("")

    var successCalled = false
    var errorCalled = false

    viewModel.loginUser(onSuccess = { successCalled = true }, onError = { errorCalled = true })

    assertFalse(successCalled)
    assertTrue(errorCalled)
  }
}
