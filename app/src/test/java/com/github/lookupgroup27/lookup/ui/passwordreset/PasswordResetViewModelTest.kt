package com.github.lookupgroup27.lookup.ui.passwordreset

import com.github.lookupgroup27.lookup.model.passwordreset.PasswordResetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class PasswordResetViewModelTest {

  private lateinit var viewModel: PasswordResetViewModel
  private lateinit var mockRepository: PasswordResetRepository

  @Before
  fun setUp() {
    mockRepository = mock()
    viewModel = PasswordResetViewModel(mockRepository)

    Dispatchers.setMain(StandardTestDispatcher())
  }

  @After
  fun tearDown() {

    Dispatchers.resetMain()
  }

  @Test
  fun `onEmailChanged updates email in uiState`() = runTest {
    viewModel.onEmailChanged("test@example.com")
    val currentState = viewModel.uiState.first()
    assertEquals("test@example.com", currentState.email)
  }

  @Test
  fun `clearFields resets email in uiState`() = runTest {
    viewModel.onEmailChanged("test@example.com")
    viewModel.clearFields()
    val currentState = viewModel.uiState.first()
    assertEquals("", currentState.email)
  }

  @Test
  fun `resetPassword triggers success when repository call succeeds`() = runTest {
    whenever(mockRepository.sendPasswordResetEmail("test@example.com"))
        .thenReturn(Result.success(Unit))

    viewModel.onEmailChanged("test@example.com")

    viewModel.resetPassword()
    advanceUntilIdle()

    val currentState = viewModel.uiState.first()
    assertTrue(currentState.isSuccess)
    assertFalse(currentState.isLoading)
    assertNull(currentState.errorMessage)
  }

  @Test
  fun `resetPassword sets errorMessage when repository call fails`() = runTest {
    whenever(mockRepository.sendPasswordResetEmail("test@example.com"))
        .thenReturn(Result.failure(Exception("Test Error")))

    viewModel.onEmailChanged("test@example.com")

    viewModel.resetPassword()
    advanceUntilIdle()

    val currentState = viewModel.uiState.first()
    assertEquals("Test Error", currentState.errorMessage)
    assertFalse(currentState.isSuccess)
    assertFalse(currentState.isLoading)
  }
}
