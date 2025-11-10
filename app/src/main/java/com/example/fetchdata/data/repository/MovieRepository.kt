package com.example.fetchdata.data.repository

import com.example.fetchdata.data.model.MovieDetail
import com.example.fetchdata.data.model.SearchResponse
import com.example.fetchdata.data.remote.RetrofitInstance
import com.example.fetchdata.utils.Constants

class MovieRepository {
    suspend fun getMovieDetails(apiKey: String, imdbId: String): MovieDetail {
        return RetrofitInstance.api.getMovieDetail(Constants.API_KEY, imdbId)
    }

    suspend fun searchMovies(searchQuery: String, page: Int): SearchResponse {
        return RetrofitInstance.api.searchMovies(Constants.API_KEY, searchQuery, page)
    }
}