package com.example.fetchdata.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.fetchdata.data.local.User
import com.example.fetchdata.data.local.UserDao

class UserRepository(private val userDao: UserDao, private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LOGGED_IN_EMAIL = "logged_in_email"
    }

    suspend fun registerUser(user: User): Boolean {
        return try {
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                false // User already exists
            } else {
                userDao.insertUser(user)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loginUser(email: String, password: String): User? {
        val user = userDao.loginUser(email, password)
        if (user != null) {
            // Save logged in state
            sharedPreferences.edit().putString(KEY_LOGGED_IN_EMAIL, email).apply()
        }
        return user
    }

    suspend fun getLoggedInUser(): User? {
        val email = sharedPreferences.getString(KEY_LOGGED_IN_EMAIL, null)
        return if (email != null) {
            userDao.getUserByEmail(email)
        } else {
            null
        }
    }

    fun logout() {
        sharedPreferences.edit().remove(KEY_LOGGED_IN_EMAIL).apply()
    }
}

