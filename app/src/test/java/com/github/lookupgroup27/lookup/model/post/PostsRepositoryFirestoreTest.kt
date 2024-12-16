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
import junit.framework.TestCase.fail
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

/**
 * Tests for the PostsRepositoryFirestore class.
 *
 * This test suite validates the functionality of the `PostsRepositoryFirestore` class.
 *
 * The tests cover:
 * - Adding a post (`addPost` method).
 * - Retrieving posts (`getPosts` method).
 * - Updating a post (`updatePost` method).
 * - Deleting a post (`deletePost` method).
 *
 * Mocking is used extensively to simulate Firestore behavior, ensuring the repository methods
 * interact with Firestore as expected.
 */
@RunWith(RobolectricTestRunner::class)
class PostsRepositoryFirestoreTest {

  // Mock dependencies for Firestore
  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockToDoQuerySnapshot: QuerySnapshot

  // Repository instance under test
  private lateinit var postsRepositoryFirestore: PostsRepositoryFirestore

  // A sample post object used in the tests
  private val post =
      Post(
          "1",
          "testUri",
          "testUsername",
          "testMail",
          10,
          2.5,
          0.0,
          0.0,
          2,
          listOf("test@gmail.com", "joedoe@gmail.com"),
          "testDescription",
          0L)

  /**
   * Sets up the test environment before each test.
   * - Mocks Firestore dependencies and initializes the `PostsRepositoryFirestore` instance.
   * - Ensures Firebase is initialized in the context using Robolectric.
   */
  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if not already initialized
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Mock Firestore behavior
    `when`(mockFirestore.collection(org.mockito.kotlin.any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(org.mockito.kotlin.any()))
        .thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)

    // Initialize the repository
    postsRepositoryFirestore = PostsRepositoryFirestore(mockFirestore)
  }

  /**
   * Tests the `addPost` method when Firestore returns a failure.
   *
   * Verifies:
   * - The `onFailure` callback is triggered with the correct error message.
   * - The `set` method is called on the Firestore document reference.
   */
  @Test
  fun `test addPost should call onFailure when firestore fails`() {
    val exception = Exception("Firestore error")
    `when`(mockDocumentReference.set(org.mockito.kotlin.any()))
        .thenReturn(Tasks.forException(exception))

    var errorMessage: String? = null
    postsRepositoryFirestore.addPost(
        post, onSuccess = {}, onFailure = { errorMessage = it.message })

    shadowOf(Looper.getMainLooper()).idle()

    assert(errorMessage == "Firestore error")
    verify(mockDocumentReference).set(org.mockito.kotlin.any())
  }

  /**
   * Tests the `addPost` method when Firestore succeeds.
   *
   * Verifies:
   * - The `onSuccess` callback is triggered.
   * - The `set` method is called on the Firestore document reference.
   */
  @Test
  fun `test addPost should call onSuccess when firestore succeeds`() {
    `when`(mockDocumentReference.set(org.mockito.kotlin.any())).thenReturn(Tasks.forResult(null))

    var successCalled = false
    postsRepositoryFirestore.addPost(post, onSuccess = { successCalled = true }, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    assert(successCalled)
    verify(mockDocumentReference).set(org.mockito.kotlin.any())
  }

  /**
   * Tests the `getPosts` method when Firestore returns data successfully.
   *
   * Verifies:
   * - The `onSuccess` callback is triggered with the correct list of posts.
   */
  @Test
  fun `getPosts should call onSuccess with list of posts when Firestore query succeeds`() {
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
            "userMail" to "test@example.com",
            "latitude" to 1.0,
            "longitude" to 1.0,
            "timestamp" to 1000L)
    val post2 =
        mapOf(
            "uid" to "2",
            "uri" to "uri2",
            "username" to "user2",
            "userMail" to "test2@example.com",
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
        .addSnapshotListener(any())

    // Test behavior
    var result: List<Post>? = null
    postsRepositoryFirestore.getPosts(
        onSuccess = { posts -> result = posts },
        onFailure = { fail("onFailure should not be called") })

    shadowOf(Looper.getMainLooper()).idle() // Process main thread tasks

    // Assertions
    assertThat(result?.size, `is`(2))
    assertThat(result?.get(0)?.uid, `is`("1"))
    assertThat(result?.get(1)?.uid, `is`("2"))
  }

  /**
   * Tests the `deletePost` method when Firestore succeeds.
   *
   * Verifies:
   * - The `onSuccess` callback is triggered.
   * - The `delete` method is called on the Firestore document reference.
   */
  @Test
  fun `deletePost should call onSuccess when firestore deletion succeeds`() {
    val postUid = "123"
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    var successCalled = false
    postsRepositoryFirestore.deletePost(
        postUid,
        onSuccess = { successCalled = true },
        onFailure = { fail("onFailure should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assert(successCalled)
    verify(mockDocumentReference).delete()
  }

  /**
   * Tests the `deletePost` method when Firestore fails.
   *
   * Verifies:
   * - The `onFailure` callback is triggered with the correct error message.
   * - The `delete` method is called on the Firestore document reference.
   */
  @Test
  fun `deletePost should call onFailure when firestore deletion fails`() {
    val postUid = "123"
    val exception =
        FirebaseFirestoreException(
            "Deletion failed", FirebaseFirestoreException.Code.PERMISSION_DENIED)
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forException(exception))

    var errorMessage: String? = null
    postsRepositoryFirestore.deletePost(
        postUid,
        onSuccess = { fail("onSuccess should not be called") },
        onFailure = { errorMessage = it.message })

    shadowOf(Looper.getMainLooper()).idle()

    assertThat(errorMessage, `is`("Deletion failed"))
    verify(mockDocumentReference).delete()
  }

  @Test
  fun `test updateDescription calls onSuccess when firestore update succeeds`() {
    val postUid = "1"
    val newDescription = "Updated description"
    var successCalled = false

    `when`(mockDocumentReference.update("description", newDescription))
        .thenReturn(Tasks.forResult(null))

    postsRepositoryFirestore.updateDescription(
        postUid = postUid,
        newDescription = newDescription,
        onSuccess = { successCalled = true },
        onFailure = { fail("onFailure should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assert(successCalled) { "onSuccess callback was not called" }
    verify(mockDocumentReference).update("description", newDescription)
  }

  @Test
  fun `test updateDescription calls onFailure when firestore update fails`() {
    val postUid = "1"
    val newDescription = "Updated description"
    val exception =
        FirebaseFirestoreException("Update failed", FirebaseFirestoreException.Code.ABORTED)
    var failureCalled = false

    `when`(mockDocumentReference.update("description", newDescription))
        .thenReturn(Tasks.forException(exception))

    postsRepositoryFirestore.updateDescription(
        postUid = postUid,
        newDescription = newDescription,
        onSuccess = { fail("onSuccess should not be called") },
        onFailure = { error ->
          failureCalled = true
          assert(error.message == "Update failed") { "Error message mismatch" }
        })

    shadowOf(Looper.getMainLooper()).idle()

    assert(failureCalled) { "onFailure callback was not called" }
    verify(mockDocumentReference).update("description", newDescription)
  }
}
