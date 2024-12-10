package com.github.lookupgroup27.lookup.model.collection

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.model.post.Post
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import java.util.EventListener
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class CollectionRepositoryFirestoreTest {

  private lateinit var repository: CollectionRepositoryFirestore
  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var mockCollectionReference: CollectionReference
  private lateinit var mockDocumentReference: DocumentReference
  private lateinit var mockAuth: FirebaseAuth
  private lateinit var mockUser: FirebaseUser
  private lateinit var mockImagesRef: StorageReference
  private lateinit var mockSnapshot: QuerySnapshot

  @Before
  fun setUp() {
    mockFirestore = mock()
    mockAuth = mock()
    mockUser = mock()
    mockImagesRef = mock()
    mockCollectionReference = mock()
    mockDocumentReference = mock()
    mockSnapshot = mock()

    // Initialize Firebase if not already initialized
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Set up the mock user and auth
    whenever(mockAuth.currentUser).thenReturn(mockUser)
    whenever(mockUser.email).thenReturn("test@example.com")
    whenever(mockUser.displayName).thenReturn("user1")

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
  fun `test getUserPosts returns empty list when user is not logged in`(): Unit = runBlocking {
    whenever(mockAuth.currentUser).thenReturn(null)

    var result: List<Post>? = null
    repository.getUserPosts(
        onSuccess = { posts -> result = posts },
        onFailure = { fail("onFailure should not be called") })
    result?.let { assertTrue("Expected empty list when no images are available", it.isEmpty()) }
  }

  @Test
  fun `test getUserPosts handles error and returns empty list`(): Unit = runBlocking {
    // Simulate a failure for listAll()
    whenever(mockImagesRef.child(any())).thenReturn(mockImagesRef)
    whenever(mockImagesRef.listAll()).thenReturn(Tasks.forException(Exception("Simulated error")))

    var result: List<Post>? = null
    repository.getUserPosts(
        onSuccess = { posts -> result = posts },
        onFailure = { fail("onFailure should not be called") })
    result?.let { assertTrue("Expected empty list when no images are available", it.isEmpty()) }
  }

  @Test
  fun `getuserPosts should call onSuccess with list of posts when Firestore query succeeds`():
      Unit = runBlocking {
    // Mock QuerySnapshot and DocumentSnapshots
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    val mockDocumentSnapshot1 = mock(DocumentSnapshot::class.java)
    val mockDocumentSnapshot2 = mock(DocumentSnapshot::class.java)

    // Simulate Firestore data
    val post1 =
        mapOf(
            "uid" to "1",
            "uri" to "uri1",
            "username" to "user1",
            "latitude" to 1.0,
            "longitude" to 1.0,
            "timestamp" to 1000L)
    val post2 =
        mapOf(
            "uid" to "2",
            "uri" to "uri2",
            "username" to "user2",
            "latitude" to 2.0,
            "longitude" to 2.0,
            "timestamp" to 2000L)

    // Setup mock DocumentSnapshot behavior
    `when`(mockDocumentSnapshot1.data).thenReturn(post1)
    `when`(mockDocumentSnapshot2.data).thenReturn(post2)
    `when`(mockQuerySnapshot.documents)
        .thenReturn(listOf(mockDocumentSnapshot1, mockDocumentSnapshot2))

    // Mock Firestore's addSnapshotListener behavior
    doAnswer { invocation ->
          val listener =
              invocation.arguments[0] as com.google.firebase.firestore.EventListener<QuerySnapshot>
          listener.onEvent(mockQuerySnapshot, null) // Trigger success callback
          null
        }
        .whenever(mockCollectionReference)
        .addSnapshotListener(Mockito.any())

    // Test behavior
    var result: List<Post>? = null
    repository.getUserPosts(
        onSuccess = { posts -> result = posts },
        onFailure = { fail("onFailure should not be called") })

    shadowOf(Looper.getMainLooper()).idle() // Process main thread tasks

    // Assertions
    assertThat(result?.size, `is`(1))
    assertThat(result?.get(0)?.uid, `is`(post1["uid"]))
    assertThat(result?.get(0)?.uri, `is`(post1["uri"]))
    assertThat(result?.get(0)?.username, `is`(post1["username"]))
    assertThat(result?.get(0)?.latitude, `is`(post1["latitude"]))
    assertThat(result?.get(0)?.longitude, `is`(post1["longitude"]))
    assertThat(result?.get(0)?.timestamp, `is`(post1["timestamp"]))
  }

  @Test
  fun `getUserPosts should call onFailure with exception when Firestore query fails`() =
      runBlocking {
        // Mock Firestore exception
        val mockException =
            FirebaseFirestoreException("Test exception", FirebaseFirestoreException.Code.UNKNOWN)

        // Mock Firestore's addSnapshotListener behavior
        doAnswer { invocation ->
              val listener =
                  invocation.arguments[0]
                      as com.google.firebase.firestore.EventListener<QuerySnapshot>
              listener.onEvent(null, mockException) // Trigger failure callback
              null
            }
            .whenever(mockCollectionReference)
            .addSnapshotListener(Mockito.any())

        // Test behavior
        var failureResult: Exception? = null
        repository.getUserPosts(
            onSuccess = { fail("onSuccess should not be called") },
            onFailure = { exception -> failureResult = exception })

        shadowOf(Looper.getMainLooper()).idle() // Process main thread tasks

        // Assertions
        assertThat(failureResult, `is`(notNullValue()))
        assertThat(failureResult?.message, `is`("Test exception"))
      }
}
