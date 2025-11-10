package com.example.fetchdata.data.model

data class MovieSearch(
    val Title: String,
    val Year: String,
    val imdbID: String,
    val Type: String,
    val Poster: String
)

data class SearchResponse(
    val Search: List<MovieSearch>?,
    val totalResults: String?,
    val Response: String
)

