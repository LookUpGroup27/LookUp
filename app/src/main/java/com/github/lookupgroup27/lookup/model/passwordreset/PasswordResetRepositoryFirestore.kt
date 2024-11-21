package com.github.lookupgroup27.lookup.model.passwordreset

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class PasswordResetRepositoryFirestore(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : PasswordResetRepository {
  override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
    return try {
      auth.sendPasswordResetEmail(email).await()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
