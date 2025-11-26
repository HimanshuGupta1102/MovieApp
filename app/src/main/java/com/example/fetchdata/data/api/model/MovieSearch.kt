package com.example.fetchdata.data.api.model

import com.google.gson.annotations.SerializedName

data class MovieSearch(
    @SerializedName("Title")
    val title: String,
    @SerializedName("Year")
    val year: String,
    @SerializedName("imdbID")
    val imdbID: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("Poster")
    val poster: String
)

data class SearchResponse(
    @SerializedName("Search")
    val search: List<MovieSearch>?,
    @SerializedName("totalResults")
    val totalResults: String?,
    @SerializedName("Response")
    val response: String
)