package com.example.productsapp.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import app.cash.turbine.test
import com.example.productsapp.data.repository.AuthRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var preferences: Preferences
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        dataStore = mockk()
        preferences = mockk()
        repository = AuthRepositoryImpl(dataStore)
    }

    @Test
    fun `login with correct credentials returns success`() = runTest {
        // Given
        coEvery { dataStore.updateData(any()) } returns preferences

        // When
        val result = repository.login("user", "password123")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `login with wrong credentials returns failure`() = runTest {
        // When
        val result = repository.login("wrong", "wrong")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
    }

    @Test
    fun `isLoggedIn returns false by default`() = runTest {
        // Given
        every { preferences[booleanPreferencesKey("is_logged_in")] } returns null
        every { dataStore.data } returns flowOf(preferences)

        // When & Then
        repository.isLoggedIn().test {
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isLoggedIn returns true when logged in`() = runTest {
        // Given
        every { preferences[booleanPreferencesKey("is_logged_in")] } returns true
        every { dataStore.data } returns flowOf(preferences)

        // When & Then
        repository.isLoggedIn().test {
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `logout calls dataStore updateData`() = runTest {
        // Given
        coEvery { dataStore.updateData(any()) } returns preferences

        // When
        repository.logout()

        // Then
        coVerify { dataStore.updateData(any()) }
    }
}