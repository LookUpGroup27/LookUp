package com.github.lookupgroup27.lookup.model.image

import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

/**
 * Tests for the FirebaseEditImageRepository class.
 *
 * This test suite validates the behavior of the `deleteImage` method in different scenarios:
 * - Successful deletion of an image.
 * - Failure when the image does not exist.
 *
 * It also verifies the correctness of the `getPathFromUrl` method for extracting file paths from
 * URLs.
 */
@RunWith(RobolectricTestRunner::class)
class FirebaseEditImageRepositoryTest {

  /** Repository instance under test. */
  private lateinit var repository: FirebaseEditImageRepository

  /**
   * Mock instances for Firebase dependencies.
   * - `firebaseStorage`: Mock for FirebaseStorage.
   * - `storageReference`: Mock for the root storage reference.
   * - `fileReference`: Mock for the specific file storage reference.
   */
  private lateinit var firebaseStorage: FirebaseStorage
  private lateinit var storageReference: StorageReference
  private lateinit var fileReference: StorageReference

  /**
   * Sets up the test environment by initializing Firebase and mocking its dependencies.
   *
   * This method ensures:
   * - Firebase is initialized if not already done.
   * - Mocked instances of FirebaseStorage and related references are configured.
   */
  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Set up mock behavior for Firebase Storage
    firebaseStorage = mock(FirebaseStorage::class.java)
    storageReference = mock(StorageReference::class.java)
    fileReference = mock(StorageReference::class.java)

    `when`(firebaseStorage.reference).thenReturn(storageReference)
    `when`(storageReference.child(anyString())).thenReturn(fileReference)

    // Initialize the repository
    repository = FirebaseEditImageRepository(firebaseStorage)
  }

  /**
   * Tests that `deleteImage` returns a success result when the image is deleted successfully.
   *
   * Verifies:
   * - The correct storage path is used.
   * - The `delete` operation is invoked.
   */
  @Test
  fun `deleteImage returns success when image is deleted successfully`() = runTest {
    val testImageUrl = "https://firebase.storage/o/images%2FtestImage.jpg?alt=media"

    // Simulate successful deletion
    `when`(fileReference.delete()).thenReturn(Tasks.forResult(null))

    val result = repository.deleteImage(testImageUrl)

    assertTrue(result.isSuccess)
    verify(storageReference).child("images/testImage.jpg") // Verify correct path was used
    verify(fileReference).delete() // Verify delete was called
  }

  /**
   * Tests that `deleteImage` returns a failure result when the image does not exist.
   *
   * Simulates:
   * - A `StorageException` with error code `ERROR_OBJECT_NOT_FOUND`.
   *
   * Verifies:
   * - The correct exception is returned with an appropriate error message.
   * - The `delete` operation is invoked.
   */
  @Test
  fun `deleteImage returns failure when image does not exist`() = runTest {
    val testImageUrl = "https://firebase.storage/o/images%2FnonExistentImage.jpg?alt=media"

    // Create a mock StorageException
    val exception: StorageException =
        mock(StorageException::class.java).apply {
          `when`(this.errorCode).thenReturn(StorageException.ERROR_OBJECT_NOT_FOUND)
          `when`(this.message).thenReturn("Image not found")
        }

    // Simulate the delete operation throwing a StorageException
    `when`(fileReference.delete()).thenReturn(Tasks.forException(exception))

    val result = repository.deleteImage(testImageUrl)

    assertTrue(result.isFailure)
    assertTrue(result.exceptionOrNull() is StorageException)
    assertEquals("Image not found", result.exceptionOrNull()?.message)
    verify(storageReference).child("images/nonExistentImage.jpg")
    verify(fileReference).delete()
  }

  /**
   * Tests the `getPathFromUrl` method for extracting the correct file path from a URL.
   *
   * Validates:
   * - The extracted path matches the expected format.
   */
  @Test
  fun `getPathFromUrl extracts correct path from URL`() {
    val testImageUrl = "https://firebase.storage/o/images%2FtestImage.jpg?alt=media"

    val path =
        repository.javaClass
            .getDeclaredMethod("getPathFromUrl", String::class.java)
            .apply { isAccessible = true }
            .invoke(repository, testImageUrl)

    assertEquals("images/testImage.jpg", path)
  }
}
