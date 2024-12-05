package com.github.lookupgroup27.lookup.model.passwordreset

import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PasswordResetRepositoryFirestoreTest {

  private lateinit var repository: PasswordResetRepositoryFirestore
  private lateinit var mockAuth: FirebaseAuth

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    mockAuth = Mockito.mock(FirebaseAuth::class.java)

    repository =
        PasswordResetRepositoryFirestore().apply {
          val field = PasswordResetRepositoryFirestore::class.java.getDeclaredField("auth")
          field.isAccessible = true
          field.set(this, mockAuth)
        }
  }

  @Test
  fun `sendPasswordResetEmail succeeds with valid email`() = runBlocking {
    Mockito.`when`(mockAuth.sendPasswordResetEmail("test@example.com"))
        .thenReturn(Tasks.forResult(null))

    val result = repository.sendPasswordResetEmail("test@example.com")

    Assert.assertTrue(result.isSuccess)
  }

  @Test
  fun `sendPasswordResetEmail fails with invalid email`() = runBlocking {
    val exception = Exception("Invalid email format")
    Mockito.`when`(mockAuth.sendPasswordResetEmail("invalid-email"))
        .thenReturn(Tasks.forException(exception))

    val result = repository.sendPasswordResetEmail("invalid-email")

    Assert.assertTrue(result.isFailure)
    assert(result.exceptionOrNull()?.message == "Invalid email format")
  }

  @Test
  fun `sendPasswordResetEmail fails when FirebaseAuth throws an exception`() = runBlocking {
    val exception = Exception("Firebase error")
    Mockito.`when`(mockAuth.sendPasswordResetEmail(anyString()))
        .thenReturn(Tasks.forException(exception))

    val result = repository.sendPasswordResetEmail("test@example.com")

    Assert.assertTrue(result.isFailure)
    assert(result.exceptionOrNull()?.message == "Firebase error")
  }
}
