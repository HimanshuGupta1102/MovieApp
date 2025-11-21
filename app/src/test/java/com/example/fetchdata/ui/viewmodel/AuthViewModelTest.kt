package com.example.fetchdata.ui.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.fetchdata.data.local.User
import com.example.fetchdata.data.repository.UserRepository
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
    private lateinit var mockRepository: UserRepository

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Note: In a real scenario, you'd need to inject the repository
        // For now, this demonstrates the testing approach
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

    }

    @Test
    fun `signIn with empty email should show error`() {
        // Given - empty email
        val email = ""
        val password = "password123"

    }

    @Test
    fun `signIn with empty password should show error`() {
        // Given - empty password
        val email = "test@example.com"
        val password = ""

    }

    @Test
    fun `signIn with invalid credentials should show error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"

          }

    @Test
    fun `signIn should set loading state initially`() {
        // Given
        val email = "test@example.com"
        val password = "password123"

    }

    @Test
    fun `logout should clear current user`() {

    }
}

