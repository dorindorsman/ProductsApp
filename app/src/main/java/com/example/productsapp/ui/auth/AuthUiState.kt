package com.example.productsapp.ui.auth

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isChecked: Boolean = false,
    val error: String? = null
)