package com.example.fetchdata.data.remote

import com.example.fetchdata.data.model.MovieDetail
import com.example.fetchdata.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApiService {
    @GET("/")
    suspend fun getMovieDetail(
        @Query("apikey") apiKey: String,
        @Query("i") imdbId: String
    ): MovieDetail
    @GET("/")
    suspend fun searchMovies(
        @Query("apikey") apiKey: String,
        @Query("s") searchQuery: String,
        @Query("page") page: Int
    ): SearchResponse
}