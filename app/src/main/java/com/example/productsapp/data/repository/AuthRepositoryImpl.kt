package com.example.productsapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.productsapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AuthRepository {

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private const val VALID_USERNAME = "user"
        private const val VALID_PASSWORD = "password123"
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    }

    override suspend fun login(username: String, password: String): Result<Unit> {
        return if (username == VALID_USERNAME && password == VALID_PASSWORD) {
            dataStore.edit { it[IS_LOGGED_IN] = true }
            Result.success(Unit)
        } else {
            Result.failure(Exception("Invalid credentials"))
        }
    }

    override suspend fun logout() {
        dataStore.edit { it[IS_LOGGED_IN] = false }
    }
}