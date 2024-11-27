package com.github.lookupgroup27.lookup.ui.register

import com.github.lookupgroup27.lookup.model.register.RegisterRepository
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
    viewModel.onPasswordChanged("password123")
    assertEquals("password123", viewModel.uiState.value.password)
  }

  @Test
  fun `clearFields resets state`() {
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("password123")
    viewModel.clearFields()
    assertEquals("", viewModel.uiState.value.email)
    assertEquals("", viewModel.uiState.value.password)
  }

  @Test
  fun `registerUser calls repository and triggers onSuccess`() = runTest {
    var successCalled = false
    var errorCalled = false

    // Set email, password, and confirmPassword
    viewModel.onEmailChanged("test@example.com")
    viewModel.onPasswordChanged("password123")
    viewModel.onConfirmPasswordChanged("password123") // Add this line for the confirm password

    viewModel.registerUser(onSuccess = { successCalled = true }, onError = { errorCalled = true })

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert that the repository was called successfully
    assertTrue(successCalled)
    assertFalse(errorCalled)
  }

  @Test
  fun `registerUser with blank email triggers onError`() = runTest {
    var successCalled = false
    var errorCalled = false

    viewModel.onPasswordChanged("password123")
    viewModel.registerUser(onSuccess = { successCalled = true }, onError = { errorCalled = true })

    assertFalse(successCalled)
    assertTrue(errorCalled)
  }
}

class MockRegisterRepository : RegisterRepository {
  override suspend fun registerUser(email: String, password: String) {}
}
