package com.example.productsapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.productsapp.domain.repository.AuthRepository
import com.example.productsapp.ui.auth.AuthViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        every { authRepository.isLoggedIn() } returns flowOf(false)
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is not logged in`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(false, state.isLoggedIn)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login with empty username shows error`() = runTest {
        viewModel.onUsernameChange("")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Please fill in all fields", state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login with empty password shows error`() = runTest {
        viewModel.onUsernameChange("user")
        viewModel.onPasswordChange("")
        viewModel.login()

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Please fill in all fields", state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login success updates isLoggedIn to true`() = runTest {
        coEvery { authRepository.login("user", "password123") } returns Result.success(Unit)

        viewModel.onUsernameChange("user")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(true, state.isLoggedIn)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login failure shows error message`() = runTest {
        coEvery {
            authRepository.login("wrong", "wrong")
        } returns Result.failure(Exception("Invalid credentials"))

        viewModel.onUsernameChange("wrong")
        viewModel.onPasswordChange("wrong")
        viewModel.login()

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Invalid credentials", state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onUsernameChange updates username and clears error`() = runTest {
        viewModel.onUsernameChange("newuser")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("newuser", state.username)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `logout calls repository logout`() = runTest {
        coEvery { authRepository.logout() } returns Unit

        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { authRepository.logout() }
    }
}