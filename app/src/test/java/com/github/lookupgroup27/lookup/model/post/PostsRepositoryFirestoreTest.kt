package com.github.lookupgroup27.lookup.model.post

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class PostsRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockToDoQuerySnapshot: QuerySnapshot

  private lateinit var postsRepositoryFirestore: PostsRepositoryFirestore

  private val post =
      Post(uid = "1", uri = "uri", username = "user", likes = 10, latitude = 0.0, longitude = 0.0)

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    `when`(mockFirestore.collection(org.mockito.kotlin.any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(org.mockito.kotlin.any()))
        .thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)

    postsRepositoryFirestore = PostsRepositoryFirestore(mockFirestore)
  }

  @Test
  fun `test addPost should call onFailure when firestore fails`() {
    val exception = Exception("Firestore error")
    `when`(mockDocumentReference.set(org.mockito.kotlin.any()))
        .thenReturn(Tasks.forException(exception)) // Simulate failure

    var errorMessage: String? = null
    postsRepositoryFirestore.addPost(
        post, onSuccess = {}, onFailure = { errorMessage = it.message })

    shadowOf(Looper.getMainLooper()).idle()

    // Ensure onFailure callback is called with the correct error
    assert(errorMessage == "Firestore error")

    // Verify that Firestore's set method was called
    verify(mockDocumentReference).set(org.mockito.kotlin.any())
  }

  @Test
  fun `test addPost should call onSuccess when firestore succeeds`() {
    `when`(mockDocumentReference.set(org.mockito.kotlin.any()))
        .thenReturn(Tasks.forResult(null)) // Simulate success

    var successCalled = false
    postsRepositoryFirestore.addPost(post, onSuccess = { successCalled = true }, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    // Ensure onSuccess callback is called
    assert(successCalled)

    // Verify that Firestore's set method was called
    verify(mockDocumentReference).set(org.mockito.kotlin.any())
  }

  @Test
  fun `test getPosts should return posts when firestore succeeds`() {
    val querySnapshot = mock(QuerySnapshot::class.java)
    val documentSnapshot = mock(DocumentSnapshot::class.java)
    val postMap =
        mapOf(
            "uid" to "1",
            "uri" to "uri",
            "username" to "user",
            "likes" to 10L,
            "latitude" to 0.0,
            "longitude" to 0.0)

    // Mock Firestore document snapshot data and behavior
    `when`(documentSnapshot.data).thenReturn(postMap)
    `when`(querySnapshot.documents).thenReturn(listOf(documentSnapshot))

    // Mock the addSnapshotListener call to immediately return a successful result
    doAnswer { invocation ->
          val listener =
              invocation.arguments[0] as com.google.firebase.firestore.EventListener<QuerySnapshot>
          listener.onEvent(querySnapshot, null) // Trigger the listener with a mock snapshot
          null
        }
        .`when`(mockCollectionReference)
        .addSnapshotListener(any())

    var fetchedPosts: List<Post>? = null
    postsRepositoryFirestore.getPosts(
        onSuccess = { posts -> fetchedPosts = posts },
        onFailure = { fail("onFailure should not be called") })

    // Assertions
    assert(fetchedPosts != null) // Assert posts are not null
    assertEquals(1, fetchedPosts?.size) // Assert correct number of posts
    assertEquals("1", fetchedPosts?.get(0)?.uid)
    assertEquals("uri", fetchedPosts?.get(0)?.uri)
    assertEquals("user", fetchedPosts?.get(0)?.username)
    assertEquals(10, fetchedPosts?.get(0)?.likes)
    assertEquals(0.0, fetchedPosts?.get(0)?.latitude)
    assertEquals(0.0, fetchedPosts?.get(0)?.longitude)
  }

  @Test
  fun `test getPosts should call onFailure when firestore fails`() {
    val exception =
        FirebaseFirestoreException("Firestore error", FirebaseFirestoreException.Code.UNKNOWN)

    // Mock the addSnapshotListener call to immediately return an error
    doAnswer { invocation ->
          val listener =
              invocation.arguments[0] as com.google.firebase.firestore.EventListener<QuerySnapshot>
          listener.onEvent(null, exception) // Trigger the listener with an error
          null
        }
        .`when`(mockCollectionReference)
        .addSnapshotListener(any())

    var errorMessage: String? = null
    postsRepositoryFirestore.getPosts(
        onSuccess = { fail("onSuccess should not be called") },
        onFailure = { errorMessage = it.message })

    // Verify the error message is as expected
    assert(errorMessage == "Firestore error")
  }

  @Test
  fun `test generateNewUid returns a new UID`() {
    `when`(mockDocumentReference.id).thenReturn("1")
    val uid = postsRepositoryFirestore.generateNewUid()
    assert(uid == "1")
  }

  @Test
  fun `test getPosts calls documents`() {
    // Ensure that mockToDoQuerySnapshot is properly initialized and mocked
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockToDoQuerySnapshot))

    // Ensure the QuerySnapshot returns a list of mock DocumentSnapshots
    `when`(mockToDoQuerySnapshot.documents).thenReturn(listOf())

    // Call the method under test
    postsRepositoryFirestore.getPosts(
        onSuccess = {

          // Do nothing; we just want to verify that the 'documents' field was accessed
        },
        onFailure = { fail("Failure callback should not be called") })

    // Verify that the 'documents' field was accessed
    org.mockito.kotlin.verify(org.mockito.kotlin.timeout(100)) { (mockToDoQuerySnapshot).documents }
  }

  @Test
  fun `test getPosts should return EmptyList when firestore has no data`() {
    val querySnapshot = mock(QuerySnapshot::class.java)
    `when`(querySnapshot.documents).thenReturn(emptyList())

    doAnswer { invocation ->
          val listener =
              invocation.arguments[0] as com.google.firebase.firestore.EventListener<QuerySnapshot>
          listener.onEvent(querySnapshot, null)
          null
        }
        .`when`(mockCollectionReference)
        .addSnapshotListener(any())

    var fetchedPosts: List<Post>? = null
    postsRepositoryFirestore.getPosts(
        onSuccess = { posts -> fetchedPosts = posts },
        onFailure = { fail("Failure callback should not be called") })

    assert(fetchedPosts?.isEmpty() == true)
  }

  @Test
  fun `test getPosts should call onFailure when network error occurs`() {
    val exception =
        FirebaseFirestoreException("Network error", FirebaseFirestoreException.Code.UNAVAILABLE)

    doAnswer { invocation ->
          val listener =
              invocation.arguments[0] as com.google.firebase.firestore.EventListener<QuerySnapshot>
          listener.onEvent(null, exception)
          null
        }
        .`when`(mockCollectionReference)
        .addSnapshotListener(any())

    var errorMessage: String? = null
    postsRepositoryFirestore.getPosts(
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { errorMessage = it.message })

    assertThat(errorMessage, `is`("Network error"))
  }
}
