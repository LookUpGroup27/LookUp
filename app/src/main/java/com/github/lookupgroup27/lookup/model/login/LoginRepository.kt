package com.github.lookupgroup27.lookup.model.login

interface LoginRepository {
  suspend fun loginUser(email: String, password: String)
}
