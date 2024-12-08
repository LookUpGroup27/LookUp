package com.github.lookupgroup27.lookup.model.register

import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
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
    val mockResult = mock(AuthResult::class.java)
    `when`(mockAuth.createUserWithEmailAndPassword("test@example.com", "Password123"))
        .thenReturn(Tasks.forResult(mockResult))

    try {
      repository.registerUser("test@example.com", "Password123")
      assertTrue(true)
    } catch (e: Exception) {
      fail("Expected registration to succeed, but it failed with exception: ${e.message}")
    }
  }

  @Test
  fun `registerUser throws UserAlreadyExistsException when email is already in use`() =
      runBlocking {
        val exception =
            FirebaseAuthUserCollisionException(
                "ERROR_EMAIL_ALREADY_IN_USE",
                "The email address is already in use by another account.")
        `when`(mockAuth.createUserWithEmailAndPassword("test@example.com", "Password123"))
            .thenReturn(Tasks.forException(exception))

        try {
          repository.registerUser("test@example.com", "Password123")
          fail("Expected UserAlreadyExistsException to be thrown")
        } catch (e: UserAlreadyExistsException) {
          assertEquals("An account with this email already exists.", e.message)
        } catch (e: Exception) {
          fail("Expected UserAlreadyExistsException, but got ${e::class.simpleName}")
        }
      }

  @Test
  fun `registerUser throws WeakPasswordException when password is weak`() = runBlocking {
    val exception =
        FirebaseAuthWeakPasswordException("ERROR_WEAK_PASSWORD", "Password is too weak", null)
    `when`(mockAuth.createUserWithEmailAndPassword("test@example.com", "weakpass"))
        .thenReturn(Tasks.forException(exception))

    try {
      repository.registerUser("test@example.com", "weakpass")
      fail("Expected WeakPasswordException to be thrown")
    } catch (e: WeakPasswordException) {
      assertEquals("Your password is too weak.", e.message)
    } catch (e: Exception) {
      fail("Expected WeakPasswordException, but got ${e::class.simpleName}")
    }
  }

  @Test
  fun `registerUser throws Exception for unknown errors`() = runBlocking {
    val exception = Exception("Unknown error")
    `when`(mockAuth.createUserWithEmailAndPassword("test@example.com", "Password123"))
        .thenReturn(Tasks.forException(exception))

    try {
      repository.registerUser("test@example.com", "Password123")
      fail("Expected Exception to be thrown")
    } catch (e: Exception) {
      assertEquals("Registration failed due to an unexpected error.", e.message)
    }
  }
}
