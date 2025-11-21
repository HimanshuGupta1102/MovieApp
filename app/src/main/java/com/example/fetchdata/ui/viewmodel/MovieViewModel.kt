package com.example.fetchdata.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fetchdata.data.local.MovieDatabase
import com.example.fetchdata.data.model.MovieDetail
import com.example.fetchdata.data.repository.MovieRepository
import com.example.fetchdata.utils.NetworkUtils
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MovieDatabase.getDatabase(application)
    private val repository = MovieRepository(database.movieCacheDao())

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
                _error.postValue(NetworkUtils.getErrorMessage(e))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}