package com.example.fetchdata.data.api.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_cache")
data class MovieCache(
    @PrimaryKey
    val imdbID: String,
    val title: String,
    val year: String,
    val poster: String,
    val type: String,
    val searchQuery: String,
    val page: Int,
    val cachedAt: Long = System.currentTimeMillis()
)

