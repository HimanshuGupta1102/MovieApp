package com.example.fetchdata.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_movies")
data class FavouriteMovie(
    @PrimaryKey
    val imdbID: String,
    val title: String,
    val year: String,
    val poster: String,
    val type: String,
    val addedAt: Long = System.currentTimeMillis()
)

