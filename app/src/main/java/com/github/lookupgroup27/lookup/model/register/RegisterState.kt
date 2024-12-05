package com.github.lookupgroup27.lookup.model.register

/**
 * Data class representing the state of the registration UI.
 *
 * This class holds the values entered by the user and any error messages related to those inputs.
 * It also maintains the loading state to indicate when a registration operation is in progress.
 *
 * @property email The email input by the user.
 * @property password The password input by the user.
 * @property confirmPassword The confirmation password input by the user.
 * @property emailError Error message related to the email input, if any.
 * @property passwordError Error message related to the password input, if any.
 * @property confirmPasswordError Error message related to the confirm password input, if any.
 * @property generalError General error message not specific to any field.
 * @property isLoading Indicates whether a registration operation is in progress.
 */
data class RegisterState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false
)
