package com.github.lookupgroup27.lookup.model.image

import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.internal.concurrent.Task
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import java.io.File

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

    /*firebaseAuth = mock(FirebaseAuth::class.java)
    firebaseStorage = mock(FirebaseStorage::class.java)
    storageReference = mock(StorageReference::class.java)
    fileReference = mock(StorageReference::class.java)
    mockUser = mock(FirebaseUser::class.java)*/

    // Set up mock behavior for FirebaseAuth and FirebaseStorage
    `when`(firebaseAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.email).thenReturn("testuser@example.com")
    `when`(firebaseStorage.reference).thenReturn(storageReference)
    `when`(storageReference.child(anyString())).thenReturn(fileReference)

    // Initialize the repository
    repository = FirebaseImageRepository(firebaseStorage,firebaseAuth)
  }

  @Test
  fun `uploadImage returns failure if user is not signed in`() = runTest {
    `when`(firebaseAuth.currentUser).thenReturn(null)

    val mockFile = mock(File::class.java)
    `when`(mockFile.name).thenReturn("testImage.jpg")

    val result = repository.uploadImage(mockFile)

    assertTrue(result.isFailure)
    assertEquals("Please sign in to upload images.", result.exceptionOrNull()?.message)
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
    val fakeUri = android.net.Uri.parse("https://fakeurl.com/testImage.jpg")
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
    assertEquals("Upload failed", result.exceptionOrNull()?.message)
  }
}
