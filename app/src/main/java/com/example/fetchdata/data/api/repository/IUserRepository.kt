package com.example.fetchdata.data.api.repository

import com.example.fetchdata.data.api.model.User

interface IUserRepository {

    suspend fun registerUser(user: User): Boolean

    suspend fun loginUser(email: String, password: String): User?

    suspend fun getLoggedInUser(): User?

    fun logout()
}