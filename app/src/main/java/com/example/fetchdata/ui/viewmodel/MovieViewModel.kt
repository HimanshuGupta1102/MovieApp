package com.example.fetchdata.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchdata.data.model.MovieDetail
import com.example.fetchdata.data.repository.MovieRepository
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    private val repository = MovieRepository()

    private val _movieDetail = MutableLiveData<MovieDetail>()
    val movieDetail: LiveData<MovieDetail> = _movieDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchMovieDetails(imdbId: String) {
        Log.d("MovieViewModel", "Fetching movie details for ID: $imdbId")
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = repository.getMovieDetails(com.example.fetchdata.utils.Constants.API_KEY, imdbId)
                Log.d("MovieViewModel", "API response received: $response")
                _movieDetail.postValue(response)
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching movie details: ${e.message}", e)

                // User-friendly error messages
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host") == true ->
                        "No internet connection"
                    e.message?.contains("timeout") == true ->
                        "Request timed out. Please try again"
                    e.message?.contains("401") == true || e.message?.contains("403") == true ->
                        "Invalid API key"
                    else -> "Error loading movie details: ${e.message}"
                }
                _error.postValue(errorMsg)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}