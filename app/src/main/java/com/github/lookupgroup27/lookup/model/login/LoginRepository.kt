package com.github.lookupgroup27.lookup.model.login

/**
 * Interface defining the contract for login operations. Implementations of this interface should
 * handle the logic for authenticating a user.
 */
interface LoginRepository {
  /**
   * Logs in a user with the provided email and password.
   *
   * @param email The user's email address.
   * @param password The user's password.
   */
  suspend fun loginUser(email: String, password: String)
}
