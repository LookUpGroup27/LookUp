package com.github.lookupgroup27.lookup.model.register

import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.*
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RegisterRepositoryFirestoreTest {

  private lateinit var repository: RegisterRepositoryFirestore
  private lateinit var mockAuth: FirebaseAuth
  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var mockCollectionRef: CollectionReference
  private lateinit var mockQuery: Query

  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    mockAuth = mock(FirebaseAuth::class.java)
    mockFirestore = mock(FirebaseFirestore::class.java)
    mockCollectionRef = mock(CollectionReference::class.java)
    mockQuery = mock(Query::class.java)

    // For any username passed to whereEqualTo, return mockQuery
    `when`(mockFirestore.collection("users")).thenReturn(mockCollectionRef)
    `when`(mockCollectionRef.whereEqualTo(eq("username"), anyString())).thenReturn(mockQuery)
    `when`(mockQuery.limit(1)).thenReturn(mockQuery)

    repository = RegisterRepositoryFirestore(mockAuth, mockFirestore)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `registerUser throws UsernameAlreadyExistsException when username is taken`() = runTest {
    val mockResult = mock(AuthResult::class.java)
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    val mockDoc = mock(DocumentSnapshot::class.java)

    // Query should not be empty for "takenUsername"
    `when`(mockQuerySnapshot.isEmpty).thenReturn(false)
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDoc))
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    `when`(mockAuth.createUserWithEmailAndPassword("test@example.com", "Password123"))
        .thenReturn(Tasks.forResult(mockResult))

    try {
      repository.registerUser("test@example.com", "Password123", "takenUsername")
      fail("Expected UsernameAlreadyExistsException to be thrown")
    } catch (e: UsernameAlreadyExistsException) {
      assertEquals("Username 'takenUsername' is already in use.", e.message)
    } catch (e: Exception) {
      fail("Expected UsernameAlreadyExistsException, but got ${e::class.simpleName}")
    }
  }

  @Test
  fun `registerUser throws UserAlreadyExistsException when email is already in use`() = runTest {
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    `when`(mockQuerySnapshot.isEmpty).thenReturn(true)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    val exception =
        FirebaseAuthUserCollisionException(
            "ERROR_EMAIL_ALREADY_IN_USE", "The email address is already in use by another account.")

    `when`(mockAuth.createUserWithEmailAndPassword("test@example.com", "Password123"))
        .thenReturn(Tasks.forException(exception))

    try {
      repository.registerUser("test@example.com", "Password123", "anotherUniqueUsername")
      fail("Expected UserAlreadyExistsException to be thrown")
    } catch (e: UserAlreadyExistsException) {
      assertEquals("An account with this email already exists.", e.message)
    } catch (e: Exception) {
      fail("Expected UserAlreadyExistsException, but got ${e::class.simpleName}")
    }
  }

  @Test
  fun `registerUser throws WeakPasswordException when password is weak`() = runTest {
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    `when`(mockQuerySnapshot.isEmpty).thenReturn(true)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    val exception =
        FirebaseAuthWeakPasswordException("ERROR_WEAK_PASSWORD", "Password is too weak", null)

    `when`(mockAuth.createUserWithEmailAndPassword("test@example.com", "weakpass"))
        .thenReturn(Tasks.forException(exception))

    try {
      repository.registerUser("test@example.com", "weakpass", "weakPassUsername")
      fail("Expected WeakPasswordException to be thrown")
    } catch (e: WeakPasswordException) {
      assertEquals("Your password is too weak.", e.message)
    } catch (e: Exception) {
      fail("Expected WeakPasswordException, but got ${e::class.simpleName}")
    }
  }

  @Test
  fun `registerUser throws Exception for unknown errors`() = runTest {
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    `when`(mockQuerySnapshot.isEmpty).thenReturn(true)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    val exception = Exception("Unknown error")

    `when`(mockAuth.createUserWithEmailAndPassword("test@example.com", "Password123"))
        .thenReturn(Tasks.forException(exception))

    try {
      repository.registerUser("test@example.com", "Password123", "errorUsername")
      fail("Expected Exception to be thrown")
    } catch (e: Exception) {
      assertEquals("Registration failed due to an unexpected error.", e.message)
    }
  }
}
