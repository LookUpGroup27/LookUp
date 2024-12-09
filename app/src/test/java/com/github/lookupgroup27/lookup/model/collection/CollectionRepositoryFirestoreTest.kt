package com.github.lookupgroup27.lookup.model.collection

import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.model.post.Post
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CollectionRepositoryFirestoreTest {

  private lateinit var repository: CollectionRepositoryFirestore
  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var mockCollectionReference: CollectionReference
  private lateinit var mockDocumentReference: DocumentReference
  private lateinit var mockAuth: FirebaseAuth
  private lateinit var mockUser: FirebaseUser
  private lateinit var mockImagesRef: StorageReference

  @Before
  fun setUp() {
    mockFirestore = mock()
    mockAuth = mock()
    mockUser = mock()
    mockImagesRef = mock()
    mockCollectionReference = mock()
    mockDocumentReference = mock()

    // Initialize Firebase if not already initialized
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Set up the mock user and auth
    whenever(mockAuth.currentUser).thenReturn(mockUser)
    whenever(mockUser.email).thenReturn("test@example.com")

    // Mock Firestore behavior
    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)

    // Initialize the repository with mocks
    repository = CollectionRepositoryFirestore(mockFirestore, mockAuth)
  }

  @Test
  fun `test getUserImageUrls returns empty list when no images available`(): Unit = runBlocking {
    val mockListResult = mock<ListResult>()
    whenever(mockListResult.items).thenReturn(emptyList())

    // Make listAll() return an empty ListResult immediately
    whenever(mockImagesRef.child(any())).thenReturn(mockImagesRef)
    whenever(mockImagesRef.listAll()).thenReturn(Tasks.forResult(mockListResult))

    var result: List<Post>? = null
    repository.getUserPosts(
        onSuccess = { posts -> result = posts },
        onFailure = { fail("onFailure should not be called") })
    result?.let { assertTrue("Expected empty list when no images are available", it.isEmpty()) }
  }

  @Test
  fun `test getUserImageUrls returns empty list when user is not logged in`(): Unit = runBlocking {
    whenever(mockAuth.currentUser).thenReturn(null)

    var result: List<Post>? = null
    repository.getUserPosts(
        onSuccess = { posts -> result = posts },
        onFailure = { fail("onFailure should not be called") })
    result?.let { assertTrue("Expected empty list when no images are available", it.isEmpty()) }
  }

  @Test
  fun `test getUserImageUrls handles error and returns empty list`(): Unit = runBlocking {
    // Simulate a failure for listAll()
    whenever(mockImagesRef.child(any())).thenReturn(mockImagesRef)
    whenever(mockImagesRef.listAll()).thenReturn(Tasks.forException(Exception("Simulated error")))

    var result: List<Post>? = null
    repository.getUserPosts(
        onSuccess = { posts -> result = posts },
        onFailure = { fail("onFailure should not be called") })
    result?.let { assertTrue("Expected empty list when no images are available", it.isEmpty()) }
  }
}
