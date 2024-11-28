package com.github.lookupgroup27.lookup.model.passwordreset

interface PasswordResetRepository {
  suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}
