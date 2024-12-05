package com.github.lookupgroup27.lookup.model.register

/**
 * Interface defining the contract for user registration repositories.
 *
 * Implementations of this interface are responsible for handling the registration logic, such as
 * interacting with authentication services like FirebaseAuth.
 */
interface RegisterRepository {

  /**
   * Registers a new user with the provided email and password.
   *
   * @param email The email address of the new user.
   * @param password The password for the new user.
   * @throws Exception If an error occurs during the registration process.
   */
  suspend fun registerUser(email: String, password: String)
}
