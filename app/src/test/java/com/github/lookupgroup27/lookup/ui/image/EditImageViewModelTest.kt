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

@OptIn(ExperimentalCoroutinesApi::class)
class EditImageViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: EditImageRepository
    private lateinit var viewModel: EditImageViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        repository = mock(EditImageRepository::class.java) // Mock the repository
        viewModel = EditImageViewModel(repository) // Inject the mock repository
        Dispatchers.setMain(testDispatcher) // Set test dispatcher
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the dispatcher after each test
    }

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

    @Test
    fun `resetState resets to Idle`() = runTest {
        viewModel.setEditImageState(EditImageState.Deleted) // Set state to Deleted
        viewModel.resetState() // Reset state
        val state = viewModel.editImageState.first()
        assertEquals(EditImageState.Idle, state) // Assert state is Idle
    }
}

