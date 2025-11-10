package com.example.fetchdata.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String
)

