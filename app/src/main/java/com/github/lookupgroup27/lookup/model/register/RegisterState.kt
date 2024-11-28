package com.github.lookupgroup27.lookup.model.register

// Represents the state of the registration screen in the application.
// Holds the values entered by the user into the email, password, and confirm password fields.
data class RegisterState(
    val email: String = "", // The user's email input.
    val password: String = "", // The user's password input.
    val confirmPassword: String = "" // The user's confirmation password input.
)
