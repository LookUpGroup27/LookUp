package com.github.lookupgroup27.lookup.model.register

import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RegisterRepositoryFirestoreTest {

  private lateinit var repository: RegisterRepositoryFirestore
  private lateinit var mockAuth: FirebaseAuth
  private lateinit var mockUser: FirebaseUser

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(androidx.test.core.app.ApplicationProvider.getApplicationContext())
        .isEmpty()) {
      FirebaseApp.initializeApp(androidx.test.core.app.ApplicationProvider.getApplicationContext())
    }

    mockAuth = mock(FirebaseAuth::class.java)

    repository = RegisterRepositoryFirestore(mockAuth)
  }

  @Test
  fun `registerUser succeeds with valid credentials`() = runBlocking {
    `when`(mockAuth.createUserWithEmailAndPassword("test@example.com", "password123"))
        .thenReturn(Tasks.forResult(mock()))

    try {
      repository.registerUser("test@example.com", "password123")
      assertTrue(true)
    } catch (e: Exception) {
      assert(false)
    }
  }

  @Test
  fun `registerUser fails with invalid credentials`() = runBlocking {
    val exception = Exception("Invalid email format")
    `when`(mockAuth.createUserWithEmailAndPassword("invalid-email", "password123"))
        .thenReturn(Tasks.forException(exception))

    try {
      repository.registerUser("invalid-email", "password123")
      assert(false)
    } catch (e: Exception) {
      assert(e.message == "Invalid email format")
    }
  }

  @Test
  fun `registerUser fails when FirebaseAuth throws exception`() = runBlocking {
    val exception = Exception("Firebase error")
    `when`(mockAuth.createUserWithEmailAndPassword(anyString(), anyString()))
        .thenReturn(Tasks.forException(exception))

    try {
      repository.registerUser("test@example.com", "password123")
      assert(false)
    } catch (e: Exception) {
      assert(e.message == "Firebase error")
    }
  }

  @Test
  fun `registerUser returns exception for empty email or password`() = runBlocking {
    try {
      repository.registerUser("", "")
      assert(false)
    } catch (e: Exception) {
      assert(true)
    }
  }
}
