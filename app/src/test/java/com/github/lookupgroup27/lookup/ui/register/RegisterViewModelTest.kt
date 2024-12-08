package com.github.lookupgroup27.lookup.ui.register

import com.github.lookupgroup27.lookup.model.register.RegisterRepository
import com.github.lookupgroup27.lookup.model.register.UserAlreadyExistsException
import com.github.lookupgroup27.lookup.model.register.WeakPasswordException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

  private lateinit var viewModel: RegisterViewModel
  private lateinit var mockRepository: MockRegisterRepository

  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    mockRepository = MockRegisterRepository()
    viewModel = RegisterViewModel(mockRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `onEmailChanged updates email state`() {
    viewModel.onEmailChanged("test@example.com")
    assertEquals("test@example.com", viewModel.uiState.value.email)
  }

  @Test
  fun `onPasswordChanged updates password state`() {
    viewModel.onPasswordChanged("Password123")
    assertEquals("Password123", viewModel.uiState.value.password)
  }

  @Test
  fun `onConfirmPasswordChanged updates confirmPassword state`() {
    viewModel.onConfirmPasswordChanged("Password123")
    assertEquals("Password123", viewModel.uiState.value.confirmPassword)
  }

  @Test
  fun `clearFields resets state`() {
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("Password123")
    viewModel.onConfirmPasswordChanged("Password123")
    viewModel.clearFields()
    assertEquals("", viewModel.uiState.value.email)
    assertEquals("", viewModel.uiState.value.password)
    assertEquals("", viewModel.uiState.value.confirmPassword)
  }

  // Validation Tests via Public Interface

  @Test
  fun `registerUser sets emailError when email is blank`() = runTest {
    viewModel.onEmailChanged("")
    viewModel.onPasswordChanged("Password1")
    viewModel.onConfirmPasswordChanged("Password1")

    viewModel.registerUser {}

    assertEquals("Email cannot be empty.", viewModel.uiState.value.emailError)
    assertNull(viewModel.uiState.value.passwordError)
    assertNull(viewModel.uiState.value.confirmPasswordError)
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun `registerUser sets emailError when email is invalid`() = runTest {
    viewModel.onEmailChanged("invalid-email")
    viewModel.onPasswordChanged("Password1")
    viewModel.onConfirmPasswordChanged("Password1")

    viewModel.registerUser {}

    assertEquals("Invalid email address.", viewModel.uiState.value.emailError)
    assertNull(viewModel.uiState.value.passwordError)
    assertNull(viewModel.uiState.value.confirmPasswordError)
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun `registerUser sets passwordError when password is too short`() = runTest {
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("Pass1")
    viewModel.onConfirmPasswordChanged("Pass1")

    viewModel.registerUser {}

    assertEquals("Password must be at least 8 characters.", viewModel.uiState.value.passwordError)
    assertNull(viewModel.uiState.value.emailError)
    assertNull(viewModel.uiState.value.confirmPasswordError)
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun `registerUser sets passwordError when password lacks digit`() = runTest {
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("Password")
    viewModel.onConfirmPasswordChanged("Password")

    viewModel.registerUser {}

    assertEquals(
        "Password must include at least one number.", viewModel.uiState.value.passwordError)
    assertNull(viewModel.uiState.value.emailError)
    assertNull(viewModel.uiState.value.confirmPasswordError)
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun `registerUser sets passwordError when password lacks uppercase letter`() = runTest {
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("password1")
    viewModel.onConfirmPasswordChanged("password1")

    viewModel.registerUser {}

    assertEquals(
        "Password must include at least one uppercase letter.",
        viewModel.uiState.value.passwordError)
    assertNull(viewModel.uiState.value.emailError)
    assertNull(viewModel.uiState.value.confirmPasswordError)
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun `registerUser sets confirmPasswordError when passwords do not match`() = runTest {
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("Password1")
    viewModel.onConfirmPasswordChanged("Password2")

    viewModel.registerUser {}

    assertEquals("Passwords do not match.", viewModel.uiState.value.confirmPasswordError)
    assertNull(viewModel.uiState.value.emailError)
    assertNull(viewModel.uiState.value.passwordError)
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun `registerUser proceeds when inputs are valid`() = runTest {
    var onSuccessCalled = false

    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("Password1")
    viewModel.onConfirmPasswordChanged("Password1")

    viewModel.registerUser { onSuccessCalled = true }

    testScheduler.advanceUntilIdle()

    assertTrue(onSuccessCalled)
    assertNull(viewModel.uiState.value.emailError)
    assertNull(viewModel.uiState.value.passwordError)
    assertNull(viewModel.uiState.value.confirmPasswordError)
    assertFalse(viewModel.uiState.value.isLoading)
  }

  // Error Handling Tests

  @Test
  fun `registerUser sets generalError when UserAlreadyExistsException is thrown`() = runTest {
    mockRepository.shouldThrowUserAlreadyExistsException = true

    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("Password1")
    viewModel.onConfirmPasswordChanged("Password1")

    viewModel.registerUser {}

    testScheduler.advanceUntilIdle()

    assertEquals("An account with this email already exists.", viewModel.uiState.value.generalError)
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun `registerUser sets passwordError when WeakPasswordException is thrown`() = runTest {
    mockRepository.shouldThrowWeakPasswordException = true

    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("Password1")
    viewModel.onConfirmPasswordChanged("Password1")

    viewModel.registerUser {}

    testScheduler.advanceUntilIdle()

    assertEquals("Your password is too weak.", viewModel.uiState.value.passwordError)
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun `registerUser sets generalError when unknown Exception is thrown`() = runTest {
    mockRepository.shouldThrowGenericException = true

    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("Password1")
    viewModel.onConfirmPasswordChanged("Password1")

    viewModel.registerUser {}

    testScheduler.advanceUntilIdle()

    assertEquals("An unexpected error occurred.", viewModel.uiState.value.generalError)
    assertFalse(viewModel.uiState.value.isLoading)
  }
}

class MockRegisterRepository : RegisterRepository {
  var shouldThrowUserAlreadyExistsException = false
  var shouldThrowWeakPasswordException = false
  var shouldThrowGenericException = false

  override suspend fun registerUser(email: String, password: String) {
    if (shouldThrowUserAlreadyExistsException) {
      throw UserAlreadyExistsException("An account with this email already exists.")
    }
    if (shouldThrowWeakPasswordException) {
      throw WeakPasswordException("Your password is too weak.")
    }
    if (shouldThrowGenericException) {
      throw Exception("An unexpected error occurred.")
    }
  }
}
