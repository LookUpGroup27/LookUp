package com.github.lookupgroup27.lookup.model.profile

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.R
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.fail
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ProfileRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockAuth: FirebaseAuth
  @Mock private lateinit var mockUser: FirebaseUser
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  private lateinit var profileRepositoryFirestore: ProfileRepositoryFirestore

  private val testUserId = "testUserId"
  private val userProfile = UserProfile("Test User", "test@example.com", "A short bio")

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Mock the Firestore collection path "users" to return a valid CollectionReference
    `when`(mockFirestore.collection("users")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    // `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
    `when`(mockAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.uid).thenReturn(testUserId)

    // Initialize the repository with the mocked Firestore instance
    profileRepositoryFirestore = ProfileRepositoryFirestore(mockFirestore, mockAuth)
  }

  @Test
  fun getUserProfile_callsGetOnDocument() = runTest {
    `when`(mockFirestore.collection("users").document(anyString()).get())
        .thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(UserProfile::class.java))
        .thenReturn(UserProfile("testUser", "test@example.com", "Sample bio"))

    profileRepositoryFirestore.getUserProfile(
        onSuccess = { profile ->
          assert(profile != null)
          assert(profile?.username == "testUser")
        },
        onFailure = { error ->
          assert(false) // Test fails if we reach here
        })
  }

  @Test
  fun updateUserProfile_callsSetOnDocument() = runTest {
    val userProfile = UserProfile("testUser", "test@example.com", "Sample bio")

    `when`(mockDocumentReference.set(userProfile)).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.updateUserProfile(
        userProfile,
        onSuccess = {
          assert(true) // Ensure update was successful
        },
        onFailure = { error ->
          assert(false) // Test fails if we reach here
        })
  }

  @Test
  fun logoutUser_callsSignOut() {
    profileRepositoryFirestore.logoutUser()

    // Ensure all async tasks have completed
    shadowOf(Looper.getMainLooper()).idle()

    verify(mockAuth).signOut() // Verify signOut was called on auth
  }

  @Test
  fun `init triggers onSuccess when user is logged in`() {
    // Arrange: Set up a logged-in user
    `when`(mockAuth.currentUser).thenReturn(mockUser)

    // Mock the success callback
    val onSuccess = mock<() -> Unit>()

    // Act: Call the `init` function
    profileRepositoryFirestore.init(onSuccess)

    // Simulate triggering the AuthStateListener to mimic a user login event
    val listenerCaptor = argumentCaptor<FirebaseAuth.AuthStateListener>()
    verify(mockAuth).addAuthStateListener(listenerCaptor.capture())
    listenerCaptor.firstValue.onAuthStateChanged(mockAuth)

    // Assert: Verify that onSuccess is invoked
    verify(onSuccess).invoke()
  }

  @Test
  fun `init does not trigger onSuccess when user is not logged in`() {
    // Simulate no logged-in user
    `when`(mockAuth.currentUser).thenReturn(null)

    // Mock the success callback
    val onSuccess = mock<() -> Unit>()

    profileRepositoryFirestore.init(onSuccess)

    // Verify that onSuccess is never called
    verify(onSuccess, never()).invoke()
  }

  @Test
  fun `getUserProfile calls onSuccess with UserProfile on successful task`() {
    // Arrange
    val expectedProfile = UserProfile("TestUser", "test@example.com", "Sample bio")
    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
    `when`(mockDocumentSnapshot.toObject(UserProfile::class.java)).thenReturn(expectedProfile)

    val task = Tasks.forResult(mockDocumentSnapshot)
    `when`(mockDocumentReference.get()).thenReturn(task)

    // Act
    profileRepositoryFirestore.getUserProfile(
        onSuccess = { profile ->
          // Assert
          assertNotNull(profile)
          assertEquals(expectedProfile, profile)
        },
        onFailure = { fail("Expected onSuccess to be called") })
  }

  @Test
  fun `getUserProfile calls onFailure on failed task`() {
    // Set up a failing task with an exception
    val exception = Exception("Firestore error")
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    // Mock callbacks
    val onSuccess = mock<(UserProfile?) -> Unit>()
    val onFailure = mock<(Exception) -> Unit>()

    profileRepositoryFirestore.getUserProfile(onSuccess, onFailure)

    // Ensure all runnables on the main looper are completed
    shadowOf(Looper.getMainLooper()).idle()

    // Verify that onFailure is called with the correct exception
    verify(onFailure).invoke(exception)
    verify(onSuccess, never()).invoke(any())
  }

  @Test
  fun `getUserProfile calls onSuccess with null when user is not logged in`() {
    // Simulate no logged-in user
    `when`(mockAuth.currentUser).thenReturn(null)

    // Mock callbacks
    val onSuccess = mock<(UserProfile?) -> Unit>()
    val onFailure = mock<(Exception) -> Unit>()

    profileRepositoryFirestore.getUserProfile(onSuccess, onFailure)

    // Ensure all runnables on the main looper are completed
    shadowOf(Looper.getMainLooper()).idle()

    // Verify that onSuccess is called with null
    verify(onSuccess).invoke(null)
    verify(onFailure, never()).invoke(any())
  }

  @Test
  fun `updateUserProfile calls onSuccess on successful Firestore operation`() = runTest {
    // Arrange: Set up a successful Task completion
    val userProfile = UserProfile("testUser", "test@example.com", "Sample bio")
    val onSuccess = mock<() -> Unit>()
    val onFailure = mock<(Exception) -> Unit>()

    `when`(mockDocumentReference.set(userProfile)).thenReturn(Tasks.forResult(null))

    // Act: Call updateUserProfile
    profileRepositoryFirestore.updateUserProfile(userProfile, onSuccess, onFailure)

    // Ensure all runnables on the main looper are completed
    shadowOf(Looper.getMainLooper()).idle()

    // Assert: Verify onSuccess was called and onFailure was not
    verify(onSuccess).invoke()
    verify(onFailure, never()).invoke(any())
  }

  @Test
  fun `updateUserProfile calls onFailure on failed Firestore operation`() {
    // Simulate a failed Firestore operation with an exception
    val exception = Exception("Update error")
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forException(exception))

    // Mock callbacks
    val onSuccess = mock<() -> Unit>()
    val onFailure = mock<(Exception) -> Unit>()

    profileRepositoryFirestore.updateUserProfile(userProfile, onSuccess, onFailure)

    // Ensure all runnables on the main looper are completed
    shadowOf(Looper.getMainLooper()).idle()

    // Verify that onFailure is called with the correct exception
    verify(onFailure).invoke(exception)
    verify(onSuccess, never()).invoke()
  }

  @Test
  fun `deleteUserProfile calls onSuccess on successful Firestore operation`() = runTest {
    // Arrange
    val userId = "testUserId"
    val userProfile = UserProfile("testUser", "test@example.com", "Sample bio")

    // Mock the current user and Firestore delete operation
    `when`(mockAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.uid).thenReturn(userId)
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    val onSuccess = mock<() -> Unit>()
    val onFailure = mock<(Exception) -> Unit>()

    // Act
    profileRepositoryFirestore.deleteUserProfile(userProfile, onSuccess, onFailure)

    // Ensure all runnables on the main looper are completed
    shadowOf(Looper.getMainLooper()).idle()

    // Assert
    verify(onSuccess).invoke() // Ensure onSuccess was called
    verify(onFailure, never()).invoke(any()) // Ensure onFailure was not called
  }

  @Test
  fun `deleteUserProfile calls onFailure on failed Firestore operation`() = runTest {
    // Arrange
    val userId = "testUserId"
    val exception = Exception("Delete error")

    // Mock the current user and Firestore delete operation
    `when`(mockAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.uid).thenReturn(userId)
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forException(exception))

    val onSuccess = mock<() -> Unit>()
    val onFailure = mock<(Exception) -> Unit>()

    // Act
    profileRepositoryFirestore.deleteUserProfile(UserProfile(), onSuccess, onFailure)

    // Ensure all runnables on the main looper are completed
    shadowOf(Looper.getMainLooper()).idle()

    // Assert
    verify(onFailure).invoke(exception) // Ensure onFailure was called with the correct exception
    verify(onSuccess, never()).invoke() // Ensure onSuccess was not called
  }

  @Test
  fun `deleteUserProfile calls onFailure when user is not logged in`() = runTest {
    // Arrange: Simulate no logged-in user
    `when`(mockAuth.currentUser).thenReturn(null)

    val onSuccess = mock<() -> Unit>()
    val onFailure = mock<(Exception) -> Unit>()

    // Act
    profileRepositoryFirestore.deleteUserProfile(UserProfile(), onSuccess, onFailure)

    // Ensure all runnables on the main looper are completed
    shadowOf(Looper.getMainLooper()).idle()

    // Assert
    verify(onFailure).invoke(any()) // Ensure onFailure is called
    verify(onSuccess, never()).invoke() // Ensure onSuccess was not called
  }

  @Test
  fun `saveSelectedAvatar verifies or creates profile and calls onSuccess on successful Firestore operation`() {
    val userId = "testUserId"
    val avatarId = R.drawable.avatar1
    val onSuccess = mock<() -> Unit>()
    val onFailure = mock<(Exception) -> Unit>()

    // Mock `verifyOrCreateProfile` to succeed
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)

    // Mock Firestore update to succeed
    `when`(mockDocumentReference.update("selectedAvatar", avatarId))
        .thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.saveSelectedAvatar(userId, avatarId, onSuccess, onFailure)

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).get() // Ensure `verifyOrCreateProfile` logic is called
    verify(mockDocumentReference).update("selectedAvatar", avatarId)
    verify(onSuccess).invoke()
    verify(onFailure, never()).invoke(any())
  }

  @Test
  fun `saveSelectedAvatar verifies or creates profile and calls onFailure on failed Firestore operation`() {
    val userId = "testUserId"
    val avatarId = R.drawable.avatar1
    val exception = Exception("Firestore update error")
    val onSuccess = mock<() -> Unit>()
    val onFailure = mock<(Exception) -> Unit>()

    // Mock `verifyOrCreateProfile` to succeed
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)

    // Mock Firestore update to fail
    `when`(mockDocumentReference.update("selectedAvatar", avatarId))
        .thenReturn(Tasks.forException(exception))

    profileRepositoryFirestore.saveSelectedAvatar(userId, avatarId, onSuccess, onFailure)

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).get() // Ensure `verifyOrCreateProfile` logic is called
    verify(mockDocumentReference).update("selectedAvatar", avatarId)
    verify(onFailure).invoke(exception)
    verify(onSuccess, never()).invoke()
  }

  @Test
  fun `getSelectedAvatar calls onSuccess with avatarId on successful Firestore operation`() {
    val userId = "testUserId"
    val avatarId = R.drawable.avatar1
    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
    val onSuccess = mock<(Int?) -> Unit>()
    val onFailure = mock<(Exception) -> Unit>()

    `when`(mockDocumentSnapshot.getLong("selectedAvatar")).thenReturn(avatarId.toLong())
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

    profileRepositoryFirestore.getSelectedAvatar(userId, onSuccess, onFailure)

    shadowOf(Looper.getMainLooper()).idle()

    verify(onSuccess).invoke(avatarId)
    verify(onFailure, never()).invoke(any())
  }

  @Test
  fun `getSelectedAvatar calls onFailure on failed Firestore operation`() {
    val userId = "testUserId"
    val exception = Exception("Firestore get error")
    val onSuccess = mock<(Int?) -> Unit>()
    val onFailure = mock<(Exception) -> Unit>()

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    profileRepositoryFirestore.getSelectedAvatar(userId, onSuccess, onFailure)

    shadowOf(Looper.getMainLooper()).idle()

    verify(onFailure).invoke(exception)
    verify(onSuccess, never()).invoke(any())
  }
}
