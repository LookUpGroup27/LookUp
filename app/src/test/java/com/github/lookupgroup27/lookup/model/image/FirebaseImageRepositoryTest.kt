package com.github.lookupgroup27.lookup.model.image

import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FirebaseImageRepositoryTest {

  @Mock private lateinit var repository: FirebaseImageRepository
  @Mock private lateinit var firebaseAuth: FirebaseAuth
  @Mock private lateinit var firebaseStorage: FirebaseStorage
  @Mock private lateinit var storageReference: StorageReference
  @Mock private lateinit var fileReference: StorageReference
  @Mock private lateinit var mockUser: FirebaseUser

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Set up mock behavior for FirebaseAuth and FirebaseStorage
    `when`(firebaseAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.email).thenReturn("testuser@example.com")
    `when`(firebaseStorage.reference).thenReturn(storageReference)
    `when`(storageReference.child(anyString())).thenReturn(fileReference)

    // Initialize the repository
    repository = FirebaseImageRepository(firebaseStorage, firebaseAuth)
  }

  @Test
  fun `uploadImage returns failure if user is not signed in`() = runTest {
    `when`(firebaseAuth.currentUser).thenReturn(null)

    val mockFile = mock(File::class.java)
    `when`(mockFile.name).thenReturn("testImage.jpg")

    val result = repository.uploadImage(mockFile)

    assertTrue(result.isFailure)
    assertEquals("Failed to upload image: User not signed in.", result.exceptionOrNull()?.message)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `uploadImage returns success with download URL`() = runTest {
    val temporaryFolder = TemporaryFolder()
    temporaryFolder.create()
    val tempFile = temporaryFolder.newFile("testImage.jpg")
    tempFile.writeText("Dummy content for testing")

    // Mock Firebase user
    `when`(firebaseAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.email).thenReturn("testuser@example.com")

    // Mock Firebase Storage behavior
    val mockUploadTask = mock(UploadTask::class.java)
    val fakeUri = android.net.Uri.parse("https://fakeurl.com/testImage.jpg")

    `when`(fileReference.putBytes(any())).thenReturn(mockUploadTask)

    // Simulate a successful upload to avoid timeout
    `when`(mockUploadTask.isSuccessful()).thenReturn(true)
    `when`(mockUploadTask.isComplete()).thenReturn(true)

    `when`(fileReference.downloadUrl).thenReturn(Tasks.forResult(fakeUri))

    val result = repository.uploadImage(tempFile)

    assertTrue(result.isSuccess)

    // Cleanup
    temporaryFolder.delete()
  }

  @Test
  fun `uploadImage returns failure when upload throws exception`() = runTest {
    val temporaryFolder = TemporaryFolder()
    temporaryFolder.create()
    val tempFile = temporaryFolder.newFile("testImage.jpg")
    tempFile.writeText("Dummy content for testing")

    `when`(firebaseAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.email).thenReturn("testuser@example.com")

    // Simulate an exception during upload
    `when`(fileReference.putBytes(any())).thenThrow(RuntimeException("Upload failed"))

    val result = repository.uploadImage(tempFile)

    assertTrue(result.isFailure)
    assertEquals("Failed to upload image: Upload failed", result.exceptionOrNull()?.message)
  }
}
