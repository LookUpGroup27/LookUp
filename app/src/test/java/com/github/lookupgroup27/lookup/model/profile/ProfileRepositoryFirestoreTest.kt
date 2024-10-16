package com.github.lookupgroup27.lookup.model.profile

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
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
  // private val userProfile = UserProfile("Test User", "test@example.com", "A short bio")

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
}
