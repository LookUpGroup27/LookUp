package com.github.lookupgroup27.lookup.model.login

/**
 * Represents the state of the login screen, containing user input values for email and password.
 *
 * @param email The email entered by the user (default is an empty string).
 * @param password The password entered by the user (default is an empty string).
 */
data class LoginState(val email: String = "", val password: String = "")
