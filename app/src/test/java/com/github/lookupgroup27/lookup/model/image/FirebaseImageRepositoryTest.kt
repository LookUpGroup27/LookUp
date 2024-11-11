package com.github.lookupgroup27.lookup.model.image

import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FirebaseImageRepositoryTest {

  private lateinit var repository: FirebaseImageRepository
  private lateinit var firebaseAuth: FirebaseAuth
  private lateinit var firebaseStorage: FirebaseStorage
  private lateinit var storageReference: StorageReference
  private lateinit var mockUser: FirebaseUser

  @Before
  fun setup() {

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    firebaseAuth = mock(FirebaseAuth::class.java)
    firebaseStorage = mock(FirebaseStorage::class.java)
    storageReference = mock(StorageReference::class.java)
    mockUser = mock(FirebaseUser::class.java)

    // Set up mock behavior for FirebaseAuth and FirebaseStorage
    `when`(firebaseAuth.currentUser).thenReturn(mockUser)
    `when`(firebaseStorage.reference).thenReturn(storageReference)

    // Initialize repository with mocked dependencies
    repository = FirebaseImageRepository()
  }

  @Test
  fun `uploadImage returns failure if user is not signed in`() = runTest {
    `when`(firebaseAuth.currentUser).thenReturn(null)

    // Use a mock for the File object
    val mockFile = mock(File::class.java)
    `when`(mockFile.name).thenReturn("testImage.jpg")

    val result = repository.uploadImage(mockFile)

    assertTrue(result.isFailure)
    assertEquals("Please sign in to upload images.", result.exceptionOrNull()?.message)
  }
}
