package com.github.lookupgroup27.lookup.model.register

/**
 * Exception thrown when attempting to register with a username that already exists in Firestore.
 *
 * This custom exception allows the application to inform the user that their chosen username is
 * unavailable, prompting them to choose a different username.
 *
 * @param message The detail message for this exception.
 */
class UsernameAlreadyExistsException(message: String) : Exception(message)
