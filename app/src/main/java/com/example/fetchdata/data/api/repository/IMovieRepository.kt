package com.example.fetchdata.data.api.repository

import com.example.fetchdata.data.api.model.MovieDetail
import com.example.fetchdata.data.api.model.MovieSearch
import com.example.fetchdata.data.impl.repository.Resource

interface IMovieRepository {

    suspend fun searchMoviesWithCache(searchQuery: String, page: Int): Resource<List<MovieSearch>>

    suspend fun getMovieDetails(apiKey: String, imdbId: String): MovieDetail

    suspend fun clearExpiredCache()

    suspend fun clearAllCache()

    suspend fun getCacheCount(): Int
}