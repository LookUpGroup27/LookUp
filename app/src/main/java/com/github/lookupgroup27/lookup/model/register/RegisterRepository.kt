package com.github.lookupgroup27.lookup.model.register

interface RegisterRepository {
  suspend fun registerUser(email: String, password: String)
}
