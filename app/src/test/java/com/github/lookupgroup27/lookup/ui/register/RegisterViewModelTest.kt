package com.github.lookupgroup27.lookup.ui.register

import com.github.lookupgroup27.lookup.model.register.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * A fake repository used for testing, allowing us to control the behavior of the registration
 * process.
 */
class MockRegisterRepository : RegisterRepository {
  var shouldThrowUsernameExistsException = false
  var shouldThrowUserAlreadyExistsException = false
  var shouldThrowWeakPasswordException = false
  var shouldThrowGenericException = false

  override suspend fun registerUser(email: String, password: String, username: String) {
    when {
      shouldThrowUsernameExistsException ->
          throw UsernameAlreadyExistsException("Username '$username' is already in use.")
      shouldThrowUserAlreadyExistsException ->
          throw UserAlreadyExistsException("An account with this email already exists.")
      shouldThrowWeakPasswordException -> throw WeakPasswordException("Your password is too weak.")
      shouldThrowGenericException ->
          throw Exception("Registration failed due to an unexpected error.")
      else -> {
        // Success scenario: do nothing
      }
    }
  }
}

@RunWith(JUnit4::class)
class RegisterViewModelTest {

  private val testDispatcher = StandardTestDispatcher()

  private lateinit var viewModel: RegisterViewModel
  private lateinit var fakeRepository: MockRegisterRepository

  @Before
  fun setup() {
    // Set the main dispatcher for the tests.
    Dispatchers.setMain(testDispatcher)

    fakeRepository = MockRegisterRepository()
    viewModel = RegisterViewModel(fakeRepository)
  }

  @After
  fun teardown() {
    // Reset the main dispatcher to the original state.
    Dispatchers.resetMain()
  }

  @Test
  fun `initial state is empty and not loading`() {
    val state = viewModel.uiState.value
    assertEquals("", state.email)
    assertEquals("", state.password)
    assertEquals("", state.confirmPassword)
    assertEquals("", state.username)
    assertFalse(state.isLoading)
    assertNull(state.emailError)
    assertNull(state.passwordError)
    assertNull(state.confirmPasswordError)
    assertNull(state.usernameError)
    assertNull(state.generalError)
  }

  @Test
  fun `onEmailChanged updates email and clears errors`() {
    viewModel.onEmailChanged("test@example.com")
    val state = viewModel.uiState.value
    assertEquals("test@example.com", state.email)
    assertNull(state.emailError)
    assertNull(state.generalError)
  }

  @Test
  fun `onPasswordChanged updates password and clears errors`() {
    viewModel.onPasswordChanged("Password123")
    val state = viewModel.uiState.value
    assertEquals("Password123", state.password)
    assertNull(state.passwordError)
    assertNull(state.generalError)
  }

  @Test
  fun `onConfirmPasswordChanged updates confirmPassword and clears errors`() {
    viewModel.onConfirmPasswordChanged("Password123")
    val state = viewModel.uiState.value
    assertEquals("Password123", state.confirmPassword)
    assertNull(state.confirmPasswordError)
    assertNull(state.generalError)
  }

  @Test
  fun `onUsernameChanged updates username and clears errors`() {
    viewModel.onUsernameChanged("uniqueUsername")
    val state = viewModel.uiState.value
    assertEquals("uniqueUsername", state.username)
    assertNull(state.usernameError)
    assertNull(state.generalError)
  }

  @Test
  fun `registerUser with valid inputs and success scenario updates state and calls onSuccess`() =
      runTest {
        viewModel.onUsernameChanged("uniqueUsername")
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("Password123")
        viewModel.onConfirmPasswordChanged("Password123")

        var successCalled = false
        viewModel.registerUser { successCalled = true }

        // Advance until all coroutines have finished
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(successCalled)
        assertNull(state.generalError)
        assertNull(state.usernameError)
        assertNull(state.emailError)
        assertNull(state.passwordError)
        assertNull(state.confirmPasswordError)
      }

  @Test
  fun `registerUser shows validation error when username is empty`() = runTest {
    // Username not entered
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("Password123")
    viewModel.onConfirmPasswordChanged("Password123")

    var successCalled = false
    viewModel.registerUser { successCalled = true }

    advanceUntilIdle()

    val state = viewModel.uiState.value
    assertFalse(state.isLoading)
    assertFalse(successCalled)
    assertEquals("Username cannot be empty.", state.usernameError)
  }

  @Test
  fun `registerUser shows validation error when email is invalid`() = runTest {
    viewModel.onUsernameChanged("uniqueUsername")
    viewModel.onEmailChanged("invalidEmail")
    viewModel.onPasswordChanged("Password123")
    viewModel.onConfirmPasswordChanged("Password123")

    var successCalled = false
    viewModel.registerUser { successCalled = true }

    advanceUntilIdle()

    val state = viewModel.uiState.value
    assertFalse(state.isLoading)
    assertFalse(successCalled)
    assertEquals("Invalid email address.", state.emailError)
  }

  @Test
  fun `registerUser shows validation error when passwords do not match`() = runTest {
    viewModel.onUsernameChanged("uniqueUsername")
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("Password123")
    viewModel.onConfirmPasswordChanged("WrongPassword")

    var successCalled = false
    viewModel.registerUser { successCalled = true }

    advanceUntilIdle()

    val state = viewModel.uiState.value
    assertFalse(state.isLoading)
    assertFalse(successCalled)
    assertEquals("Passwords do not match.", state.confirmPasswordError)
  }

  @Test
  fun `registerUser shows validation error when password is weak`() = runTest {
    viewModel.onUsernameChanged("uniqueUsername")
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("pass") // too short
    viewModel.onConfirmPasswordChanged("pass")

    var successCalled = false
    viewModel.registerUser { successCalled = true }

    advanceUntilIdle()

    val state = viewModel.uiState.value
    assertFalse(state.isLoading)
    assertFalse(successCalled)
    assertEquals("Password must be at least 8 characters.", state.passwordError)
  }

  @Test
  fun `registerUser handles UsernameAlreadyExistsException`() = runTest {
    fakeRepository.shouldThrowUsernameExistsException = true

    viewModel.onUsernameChanged("takenUsername")
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("Password123")
    viewModel.onConfirmPasswordChanged("Password123")

    var successCalled = false
    viewModel.registerUser { successCalled = true }

    advanceUntilIdle()

    val state = viewModel.uiState.value
    assertFalse(state.isLoading)
    assertFalse(successCalled)
    assertEquals("Username 'takenUsername' is already in use.", state.usernameError)
  }

  @Test
  fun `registerUser handles UserAlreadyExistsException`() = runTest {
    fakeRepository.shouldThrowUserAlreadyExistsException = true

    viewModel.onUsernameChanged("uniqueUsername")
    viewModel.onEmailChanged("taken@example.com")
    viewModel.onPasswordChanged("Password123")
    viewModel.onConfirmPasswordChanged("Password123")

    var successCalled = false
    viewModel.registerUser { successCalled = true }

    advanceUntilIdle()

    val state = viewModel.uiState.value
    assertFalse(state.isLoading)
    assertFalse(successCalled)
    assertEquals("An account with this email already exists.", state.generalError)
  }

  @Test
  fun `registerUser handles WeakPasswordException`() = runTest {
    fakeRepository.shouldThrowWeakPasswordException = true

    viewModel.onUsernameChanged("uniqueUsername")
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("WeakPass1")
    viewModel.onConfirmPasswordChanged("WeakPass1")

    var successCalled = false
    viewModel.registerUser { successCalled = true }

    advanceUntilIdle()

    val state = viewModel.uiState.value
    assertFalse(state.isLoading)
    assertFalse(successCalled)
    assertEquals("Your password is too weak.", state.passwordError)
  }

  @Test
  fun `registerUser handles generic Exception`() = runTest {
    fakeRepository.shouldThrowGenericException = true

    viewModel.onUsernameChanged("uniqueUsername")
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("Password123")
    viewModel.onConfirmPasswordChanged("Password123")

    var successCalled = false
    viewModel.registerUser { successCalled = true }

    advanceUntilIdle()

    val state = viewModel.uiState.value
    assertFalse(state.isLoading)
    assertFalse(successCalled)
    assertEquals("Registration failed due to an unexpected error.", state.generalError)
  }

  @Test
  fun `clearFields resets state`() = runTest {
    viewModel.onUsernameChanged("testUser")
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("Password123")
    viewModel.onConfirmPasswordChanged("Password123")

    viewModel.clearFields()
    val state = viewModel.uiState.value

    assertEquals("", state.username)
    assertEquals("", state.email)
    assertEquals("", state.password)
    assertEquals("", state.confirmPassword)
    assertNull(state.usernameError)
    assertNull(state.emailError)
    assertNull(state.passwordError)
    assertNull(state.confirmPasswordError)
    assertNull(state.generalError)
  }
}
