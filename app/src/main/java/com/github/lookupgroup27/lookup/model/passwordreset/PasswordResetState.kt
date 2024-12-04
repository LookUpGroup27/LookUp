package com.github.lookupgroup27.lookup.model.passwordreset

/**
 * Represents the UI state for the Password Reset feature.
 *
 * This state is used to manage the data and UI elements on the Password Reset screen.
 *
 * @property email The email address entered by the user for password reset.
 * @property isLoading Indicates whether the password reset operation is currently in progress.
 * @property isSuccess Indicates whether the password reset email was sent successfully.
 * @property errorMessage Stores any error message to display if the password reset operation fails.
 */
data class PasswordResetState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
