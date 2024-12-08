package com.github.lookupgroup27.lookup.model.register

/**
 * Exception thrown when attempting to register a user with an email that already exists.
 *
 * This custom exception helps in providing a clear and specific error message to the user
 * interface, allowing for better user experience and error handling.
 *
 * @param message The detail message for this exception.
 */
class UserAlreadyExistsException(message: String) : Exception(message)
