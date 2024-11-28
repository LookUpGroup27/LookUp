package com.github.lookupgroup27.lookup.model.passwordreset

data class PasswordResetState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
