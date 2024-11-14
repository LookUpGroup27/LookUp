package com.github.lookupgroup27.lookup.model.collection

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class CollectionRepositoryFirestoreTest {

  private lateinit var repository: CollectionRepositoryFirestore
  private lateinit var mockStorage: FirebaseStorage
  private lateinit var mockAuth: FirebaseAuth
  private lateinit var mockUser: FirebaseUser
  private lateinit var mockImagesRef: StorageReference

  @Before
  fun setUp() {
    mockStorage = mock()
    mockAuth = mock()
    mockUser = mock()
    mockImagesRef = mock()

    // Set up the mock user and auth
    whenever(mockAuth.currentUser).thenReturn(mockUser)
    whenever(mockUser.email).thenReturn("test@example.com")

    // Configure the FirebaseStorage reference
    whenever(mockStorage.reference).thenReturn(mockImagesRef)

    // Initialize the repository with mocks
    repository = CollectionRepositoryFirestore(mockStorage, mockAuth)
  }

  @Test
  fun `test getUserImageUrls returns empty list when no images available`() = runBlocking {
    val mockListResult = mock<ListResult>()
    whenever(mockListResult.items).thenReturn(emptyList())

    // Make listAll() return an empty ListResult immediately
    whenever(mockImagesRef.child(any())).thenReturn(mockImagesRef)
    whenever(mockImagesRef.listAll()).thenReturn(Tasks.forResult(mockListResult))

    val result = repository.getUserImageUrls()
    assertTrue("Expected empty list when no images are available", result.isEmpty())
  }

  @Test
  fun `test getUserImageUrls returns empty list when user is not logged in`() = runBlocking {
    whenever(mockAuth.currentUser).thenReturn(null)

    val result = repository.getUserImageUrls()
    assertTrue("Expected empty list when no user is logged in", result.isEmpty())
  }

  @Test
  fun `test getUserImageUrls handles error and returns empty list`() = runBlocking {
    // Simulate a failure for listAll()
    whenever(mockImagesRef.child(any())).thenReturn(mockImagesRef)
    whenever(mockImagesRef.listAll()).thenReturn(Tasks.forException(Exception("Simulated error")))

    val result = repository.getUserImageUrls()
    assertTrue("Expected empty list when an error occurs", result.isEmpty())
  }
}
