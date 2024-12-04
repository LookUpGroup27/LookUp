package com.github.lookupgroup27.lookup.model.passwordreset

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
class PasswordResetRepositoryFirestoreTest {

  private lateinit var repository: PasswordResetRepositoryFirestore
  private lateinit var mockAuth: FirebaseAuth

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(androidx.test.core.app.ApplicationProvider.getApplicationContext())
        .isEmpty()) {
      FirebaseApp.initializeApp(androidx.test.core.app.ApplicationProvider.getApplicationContext())
    }

    mockAuth = mock(FirebaseAuth::class.java)

    repository =
        PasswordResetRepositoryFirestore().apply {
          val field = PasswordResetRepositoryFirestore::class.java.getDeclaredField("auth")
          field.isAccessible = true
          field.set(this, mockAuth)
        }
  }

  @Test
  fun `sendPasswordResetEmail succeeds with valid email`() = runBlocking {
    `when`(mockAuth.sendPasswordResetEmail("test@example.com")).thenReturn(Tasks.forResult(null))

    val result = repository.sendPasswordResetEmail("test@example.com")

    assertTrue(result.isSuccess)
  }

  @Test
  fun `sendPasswordResetEmail fails with invalid email`() = runBlocking {
    val exception = Exception("Invalid email format")
    `when`(mockAuth.sendPasswordResetEmail("invalid-email"))
        .thenReturn(Tasks.forException(exception))

    val result = repository.sendPasswordResetEmail("invalid-email")

    assertTrue(result.isFailure)
    assert(result.exceptionOrNull()?.message == "Invalid email format")
  }

  @Test
  fun `sendPasswordResetEmail fails when FirebaseAuth throws an exception`() = runBlocking {
    val exception = Exception("Firebase error")
    `when`(mockAuth.sendPasswordResetEmail(anyString())).thenReturn(Tasks.forException(exception))

    val result = repository.sendPasswordResetEmail("test@example.com")

    assertTrue(result.isFailure)
    assert(result.exceptionOrNull()?.message == "Firebase error")
  }
}
