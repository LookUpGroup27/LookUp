package com.github.lookupgroup27.lookup.ui.image

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.lookupgroup27.lookup.model.image.FirebaseImageRepository
import java.io.File
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
class ImageViewModelTest {

  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

  private lateinit var repository: FirebaseImageRepository
  private lateinit var viewModel: ImageViewModel
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setup() {
    // Mock FirebaseImageRepository
    repository = mock(FirebaseImageRepository::class.java)
    viewModel = ImageViewModel(repository)
    Dispatchers.setMain(testDispatcher)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `uploadImage success`() = runTest {
    val testFile = mock(File::class.java)
    val fakeUrl = "https://fakeurl.com/image.jpg"

    // Mock repository response for a successful upload
    `when`(repository.uploadImage(testFile)).thenReturn(Result.success(fakeUrl))

    viewModel.uploadImage(testFile)
    advanceUntilIdle()

    val uploadStatus = viewModel.uploadStatus.first()
    assertEquals(false, uploadStatus.isLoading)
    assertEquals(fakeUrl, uploadStatus.downloadUrl)
    assertEquals(null, uploadStatus.errorMessage)
  }

  @Test
  fun `uploadImage failure`() = runTest {
    val testFile = mock(File::class.java)
    val errorMessage = "Upload failed"

    // Mock repository response for a failed upload
    `when`(repository.uploadImage(testFile)).thenReturn(Result.failure(Exception(errorMessage)))

    viewModel.uploadImage(testFile)
    advanceUntilIdle()

    val uploadStatus = viewModel.uploadStatus.first()
    assertEquals(false, uploadStatus.isLoading)
    assertEquals(null, uploadStatus.downloadUrl)
    assertEquals(errorMessage, uploadStatus.errorMessage)
  }

  @Test
  fun `resetUploadStatus resets the upload status`() = runTest {
    viewModel.resetUploadStatus()
    val uploadStatus = viewModel.uploadStatus.first()
    assertEquals(false, uploadStatus.isLoading)
    assertEquals(null, uploadStatus.downloadUrl)
    assertEquals(null, uploadStatus.errorMessage)
  }
}
