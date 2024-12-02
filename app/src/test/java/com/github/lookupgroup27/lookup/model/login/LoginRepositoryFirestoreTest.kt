package com.github.lookupgroup27.lookup.model.login

import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LoginRepositoryFirestoreTest {

  private lateinit var repository: LoginRepositoryFirestore
  private lateinit var mockAuth: FirebaseAuth

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(androidx.test.core.app.ApplicationProvider.getApplicationContext())
        .isEmpty()) {
      FirebaseApp.initializeApp(androidx.test.core.app.ApplicationProvider.getApplicationContext())
    }

    mockAuth = mock(FirebaseAuth::class.java)

    repository = LoginRepositoryFirestore(mockAuth)
  }

  @Test
  fun `loginUser succeeds with valid credentials`() = runBlocking {
    `when`(mockAuth.signInWithEmailAndPassword("test@example.com", "password123"))
        .thenReturn(Tasks.forResult(mock()))

    try {
      repository.loginUser("test@example.com", "password123")
      assertTrue(true)
    } catch (e: Exception) {
      assert(false)
    }
  }

  @Test
  fun `loginUser fails with invalid credentials`() = runBlocking {
    val exception = Exception("Invalid credentials")
    `when`(mockAuth.signInWithEmailAndPassword("test@example.com", "wrongpassword"))
        .thenReturn(Tasks.forException(exception))

    try {
      repository.loginUser("test@example.com", "wrongpassword")
      assert(false)
    } catch (e: Exception) {
      assert(e.message == "Invalid credentials")
    }
  }

  @Test
  fun `loginUser fails when FirebaseAuth throws exception`() = runBlocking {
    val exception = Exception("Firebase error")
    `when`(mockAuth.signInWithEmailAndPassword(anyString(), anyString()))
        .thenReturn(Tasks.forException(exception))

    try {
      repository.loginUser("test@example.com", "password123")
      assert(false)
    } catch (e: Exception) {
      assert(e.message == "Firebase error")
    }
  }

  @Test
  fun `loginUser returns exception for empty email or password`() = runBlocking {
    try {
      repository.loginUser("", "")
      assert(false)
    } catch (e: Exception) {
      assert(true)
    }
  }
}
