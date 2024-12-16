package com.github.lookupgroup27.lookup.model.register

/**
 * Exception thrown when the provided username is already taken by another user.
 *
 * This custom exception allows the application to inform the user that their desired username is
 * already in use, prompting them to choose a different one.
 *
 * @param message The detail message for this exception.
 */
class UsernameAlreadyExistsException(message: String) : Exception(message)
