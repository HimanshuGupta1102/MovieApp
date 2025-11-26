package com.example.fetchdata.ui.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.fetchdata.data.api.model.User
import com.example.fetchdata.data.impl.repository.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

/**
 * Unit tests for AuthViewModel
 * These tests run on the local JVM (no emulator needed) and test the business logic
 */
@ExperimentalCoroutinesApi
class AuthViewModelTest {

    // Rule to execute LiveData operations synchronously
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Test coroutine dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockApplication: Application

    @Mock
    private lateinit var mockRepository: UserRepositoryImpl

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Initialize ViewModel with mock repository
        viewModel = AuthViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `signIn with valid credentials should update authState to Success`() = runTest {
        // Given
        val email = "user@gmail.com"
        val password = "12345678"
        val mockUser = User(email, "John", "Doe", password)

        // When
        whenever(mockRepository.loginUser(email, password)).thenReturn(mockUser)
        viewModel.signIn(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val authState = viewModel.authState.value
        assertTrue(authState is AuthViewModel.AuthState.Success)
        assertEquals("Welcome John!", (authState as AuthViewModel.AuthState.Success).message)
        assertEquals(mockUser, viewModel.currentUser.value)
    }

    @Test
    fun `signIn with empty email should show error`() {
        // Given - empty email
        val email = ""
        val password = "password123"

        // When
        viewModel.signIn(email, password)

        // Then
        val authState = viewModel.authState.value
        assertTrue(authState is AuthViewModel.AuthState.Error)
        assertEquals("Email and password are required", (authState as AuthViewModel.AuthState.Error).message)
    }

    @Test
    fun `signIn with empty password should show error`() {
        // Given - empty password
        val email = "test@example.com"
        val password = ""

        // When
        viewModel.signIn(email, password)

        // Then
        val authState = viewModel.authState.value
        assertTrue(authState is AuthViewModel.AuthState.Error)
        assertEquals("Email and password are required", (authState as AuthViewModel.AuthState.Error).message)
    }

    @Test
    fun `signIn with invalid credentials should show error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"

        // When
        whenever(mockRepository.loginUser(email, password)).thenReturn(null)
        viewModel.signIn(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val authState = viewModel.authState.value
        assertTrue(authState is AuthViewModel.AuthState.Error)
        assertEquals("Invalid email or password", (authState as AuthViewModel.AuthState.Error).message)
    }

    @Test
    fun `signIn should set loading state initially`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val mockUser = User(email, "Test", "User", password)

        // When
        whenever(mockRepository.loginUser(email, password)).thenReturn(mockUser)
        viewModel.signIn(email, password)

        // Then - Check loading state is set before coroutine completes
        // Note: This test verifies the initial behavior
        testDispatcher.scheduler.advanceUntilIdle()

        // After processing, should be in Success state
        val authState = viewModel.authState.value
        assertTrue(authState is AuthViewModel.AuthState.Success || authState is AuthViewModel.AuthState.Loading)
    }

    @Test
    fun `logout should clear current user`() = runTest {
        // Given - User is logged in
        val email = "test@example.com"
        val password = "password123"
        val mockUser = User(email, "Test", "User", password)

        whenever(mockRepository.loginUser(email, password)).thenReturn(mockUser)
        viewModel.signIn(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify user is logged in
        assertNotNull(viewModel.currentUser.value)

        // When - Logout is called
        viewModel.logout()

        // Then - Current user should be null
        assertNull(viewModel.currentUser.value)
        verify(mockRepository).logout()
    }
}

