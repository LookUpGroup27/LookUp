package com.github.lookupgroup27.lookup.model.register

/**
 * Interface defining the contract for user registration repositories.
 *
 * Implementations of this interface are responsible for handling the registration logic, such as
 * interacting with authentication services like FirebaseAuth, as well as Firestore for username
 * uniqueness checks.
 */
interface RegisterRepository {

  /**
   * Registers a new user with the provided email, password, and username.
   *
   * This method also ensures that the chosen username is unique among existing users.
   *
   * @param email The email address of the new user.
   * @param password The password for the new user.
   * @param username The desired unique username for the new user.
   * @throws UsernameAlreadyExistsException If the username is already associated with an existing
   *   user.
   * @throws UserAlreadyExistsException If the email is already associated with an existing account.
   * @throws WeakPasswordException If the password does not meet Firebase's security requirements.
   * @throws Exception For any other unexpected errors during the registration process.
   */
  suspend fun registerUser(email: String, password: String, username: String)
}
