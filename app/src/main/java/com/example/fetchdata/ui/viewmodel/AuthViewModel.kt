package com.example.fetchdata.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchdata.data.api.model.User
import com.example.fetchdata.data.api.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: IUserRepository
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser


    fun signUp(firstName: String, lastName: String, email: String, password: String) {
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Invalid email address")
            return
        }

        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = User(email, firstName, lastName, password)
            val success = repository.registerUser(user)

            if (success) {
                // Automatically log the user in after successful registration
                val loggedInUser = repository.loginUser(email, password)
                if (loggedInUser != null) {
                    _currentUser.value = loggedInUser
                    _authState.value = AuthState.Success("Welcome ${loggedInUser.firstName}!")
                } else {
                    _currentUser.value = user
                    _authState.value = AuthState.Success("Sign up successful!")
                }
            } else {
                _authState.value = AuthState.Error("Email already registered")
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password are required")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = repository.loginUser(email, password)

            if (user != null) {
                _currentUser.value = user
                _authState.value = AuthState.Success("Welcome ${user.firstName}!")
            } else {
                _authState.value = AuthState.Error("Invalid email or password")
            }
        }
    }

    fun checkIfUserLoggedIn() {
        viewModelScope.launch {
            val user = repository.getLoggedInUser()
            _currentUser.value = user
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
    }

    sealed class AuthState {
        object Loading : AuthState()
        data class Success(val message: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}

