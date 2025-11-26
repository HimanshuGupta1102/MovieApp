package com.example.fetchdata.data.impl.remote.api

import com.example.fetchdata.data.api.model.MovieDetail
import com.example.fetchdata.data.api.model.SearchResponse
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