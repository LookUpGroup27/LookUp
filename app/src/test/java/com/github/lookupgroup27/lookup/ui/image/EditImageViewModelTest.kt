package com.github.lookupgroup27.lookup.ui.image

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.lookupgroup27.lookup.model.image.EditImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

/**
 * Tests for the EditImageViewModel class.
 *
 * This test suite ensures that the `EditImageViewModel` behaves correctly when interacting with the
 * `EditImageRepository`. It validates the ViewModel's ability to:
 * - Handle success and failure responses from the `deleteImage` method.
 * - Maintain the correct state flow (`editImageState`) during operations.
 * - Reset its state to `Idle` when required.
 *
 * Coroutines and a test dispatcher are used to simulate asynchronous operations.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EditImageViewModelTest {

  /** Ensures that LiveData operations run synchronously during testing. */
  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

  /** Mocked repository instance to simulate data layer interactions. */
  private lateinit var repository: EditImageRepository

  /** The ViewModel under test. */
  private lateinit var viewModel: EditImageViewModel

  /** A test dispatcher for controlling coroutine execution in tests. */
  private val testDispatcher = StandardTestDispatcher()

  /**
   * Sets up the test environment before each test.
   * - Mocks the repository.
   * - Initializes the ViewModel with the mocked repository.
   * - Sets the main dispatcher to the test dispatcher for coroutine control.
   */
  @Before
  fun setup() {
    repository = mock(EditImageRepository::class.java) // Mock the repository
    viewModel = EditImageViewModel(repository) // Inject the mock repository
    Dispatchers.setMain(testDispatcher) // Set test dispatcher
  }

  /**
   * Cleans up the test environment after each test.
   *
   * Resets the main dispatcher to avoid affecting other tests.
   */
  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the dispatcher after each test
  }

  /**
   * Verifies that the `deleteImage` method updates the state to `Deleted` on success.
   * - Mocks the repository to return a successful result.
   * - Calls the `deleteImage` method with a test image URL.
   * - Asserts that the state is updated to `Deleted`.
   * - Verifies that the repository's `deleteImage` method was called with the correct URL.
   */
  @Test
  fun `deleteImage success`() = runTest {
    val testImageUrl = "https://fakeurl.com/image.jpg"

    // Mock repository response for a successful deletion
    `when`(repository.deleteImage(testImageUrl)).thenReturn(Result.success(Unit))

    viewModel.deleteImage(testImageUrl)
    advanceUntilIdle()

    val state = viewModel.editImageState.first()
    assertEquals(EditImageState.Deleted, state) // Assert that state is Deleted
    verify(repository).deleteImage(testImageUrl) // Verify repository method was called
  }

  /**
   * Verifies that the `deleteImage` method updates the state to `Error` on failure.
   * - Mocks the repository to return a failed result with an error message.
   * - Calls the `deleteImage` method with a test image URL.
   * - Asserts that the state is updated to `Error` with the correct error message.
   * - Verifies that the repository's `deleteImage` method was called with the correct URL.
   */
  @Test
  fun `deleteImage failure`() = runTest {
    val testImageUrl = "https://fakeurl.com/image.jpg"
    val errorMessage = "Deletion failed"

    // Mock repository response for a failed deletion
    `when`(repository.deleteImage(testImageUrl)).thenReturn(Result.failure(Exception(errorMessage)))

    viewModel.deleteImage(testImageUrl)
    advanceUntilIdle()

    val state = viewModel.editImageState.first()
    assert(state is EditImageState.Error && (state as EditImageState.Error).message == errorMessage)
    verify(repository).deleteImage(testImageUrl)
  }

  /**
   * Verifies that the `resetState` method resets the state to `Idle`.
   * - Sets the state to `Deleted`.
   * - Calls the `resetState` method.
   * - Asserts that the state is updated to `Idle`.
   */
  @Test
  fun `resetState resets to Idle`() = runTest {
    viewModel.setEditImageState(EditImageState.Deleted) // Set state to Deleted
    viewModel.resetState() // Reset state
    val state = viewModel.editImageState.first()
    assertEquals(EditImageState.Idle, state) // Assert state is Idle
  }
}
