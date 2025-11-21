package com.example.fetchdata.data.model

import androidx.room.Entity

@Entity(
    tableName = "favourite_movies",
    primaryKeys = ["imdbID", "userEmail"]
)
data class FavouriteMovie(
    val imdbID: String,
    val userEmail: String,
    val title: String,
    val year: String,
    val poster: String,
    val type: String,
    val addedAt: Long = System.currentTimeMillis()
)