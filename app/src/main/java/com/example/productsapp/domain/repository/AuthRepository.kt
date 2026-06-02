package com.example.productsapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun isLoggedIn(): Flow<Boolean>
    suspend fun login(username: String, password: String): Result<Unit>
    suspend fun logout()

    suspend fun loginWithBiometric(): Result<Unit>
}