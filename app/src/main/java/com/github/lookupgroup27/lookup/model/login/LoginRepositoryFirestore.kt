package com.github.lookupgroup27.lookup.model.login

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class LoginRepositoryFirestore(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) :
    LoginRepository {

  override suspend fun loginUser(email: String, password: String) {
    auth.signInWithEmailAndPassword(email, password).await()
  }
}
