package com.example.fetchdata.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.fetchdata.data.local.User
import com.example.fetchdata.data.local.UserDao

class UserRepository(private val userDao: UserDao, private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "UserRepository"
        private const val KEY_LOGGED_IN_EMAIL = "logged_in_email"
    }

    suspend fun registerUser(user: User): Boolean {
        return try {
            Log.d(TAG, "Attempting to register user: ${user.email}")
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                Log.d(TAG, "User already exists: ${user.email}")
                false // User already exists
            } else {
                userDao.insertUser(user)
                Log.d(TAG, "User registered successfully: ${user.email}")
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error registering user: ${e.message}", e)
            false
        }
    }

    suspend fun loginUser(email: String, password: String): User? {
        return try {
            Log.d(TAG, "Attempting login for: $email")
            val user = userDao.loginUser(email, password)
            if (user != null) {
                // Save logged in state
                sharedPreferences.edit().putString(KEY_LOGGED_IN_EMAIL, email).apply()
                Log.d(TAG, "Login successful for: $email")
            } else {
                Log.d(TAG, "Login failed for: $email - invalid credentials")
            }
            user
        } catch (e: Exception) {
            Log.e(TAG, "Error during login: ${e.message}", e)
            null
        }
    }

    suspend fun getLoggedInUser(): User? {
        return try {
            val email = sharedPreferences.getString(KEY_LOGGED_IN_EMAIL, null)
            if (email != null) {
                val user = userDao.getUserByEmail(email)
                Log.d(TAG, "Retrieved logged in user: $email")
                user
            } else {
                Log.d(TAG, "No logged in user found")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting logged in user: ${e.message}", e)
            null
        }
    }

    fun logout() {
        try {
            val email = sharedPreferences.getString(KEY_LOGGED_IN_EMAIL, null)
            sharedPreferences.edit().remove(KEY_LOGGED_IN_EMAIL).apply()
            Log.d(TAG, "User logged out: $email")
        } catch (e: Exception) {
            Log.e(TAG, "Error during logout: ${e.message}", e)
        }
    }
}

