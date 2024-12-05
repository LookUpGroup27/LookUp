package com.github.lookupgroup27.lookup.model.register

/**
 * Exception thrown when the provided password does not meet the required strength criteria.
 *
 * This custom exception allows the application to inform the user that their password is too weak,
 * encouraging them to choose a stronger password that meets security requirements.
 *
 * @param message The detail message for this exception.
 */
class WeakPasswordException(message: String) : Exception(message)
