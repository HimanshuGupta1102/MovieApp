package com.example.fetchdata.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchdata.data.model.MovieSearch
import com.example.fetchdata.data.repository.MovieRepository
import kotlinx.coroutines.launch

class AllMoviesViewModel : ViewModel() {
    private val repository = MovieRepository()
    private val _movies = MutableLiveData<List<MovieSearch>>()
    val movies: LiveData<List<MovieSearch>> = _movies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentPage = 1
    private var currentQuery = "movie"
    private var totalResults = 0
    private val allMovies = mutableListOf<MovieSearch>()
    private var isLoadingMore = false

    init {
        searchMovies("movie")
    }

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _error.postValue("Please enter a search query")
            return
        }

        if (query == currentQuery && currentPage == 1 && _isLoading.value == true) {
            return
        }

        currentQuery = query
        currentPage = 1
        totalResults = 0
        allMovies.clear()
        fetchMovies()
    }

    fun loadMoreMovies() {
        if (isLoadingMore || _isLoading.value == true) {
            Log.d("AllMoviesViewModel", "Already loading, skipping")
            return
        }

        if (allMovies.size >= totalResults && totalResults > 0) {
            Log.d("AllMoviesViewModel", "All results loaded: ${allMovies.size}/$totalResults")
            _error.postValue("No more results available")
            return
        }

        currentPage++
        fetchMovies()
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            try {
                _isLoading.postValue(true)
                _error.postValue(null)
                isLoadingMore = true

                val searchResponse = repository.searchMovies(currentQuery, currentPage)

                if (searchResponse.Response == "True") {
                    totalResults = searchResponse.totalResults?.toIntOrNull() ?: 0
                    Log.d("AllMoviesViewModel", "Total results: $totalResults")

                    val newMovies = searchResponse.Search ?: emptyList()

                    if (currentPage == 1) {
                        allMovies.clear()
                    }

                    allMovies.addAll(newMovies)
                    _movies.postValue(allMovies.toList())

                    Log.d("AllMoviesViewModel", "Added ${newMovies.size} movies. Total: ${allMovies.size}")

                    if (newMovies.isEmpty() && currentPage > 1) {
                        _error.postValue("No more results available")
                    }
                } else {
                    val errorMessage = "No movies found"
                    _error.postValue(errorMessage)
                    if (currentPage == 1) {
                        allMovies.clear()
                        _movies.postValue(emptyList())
                    }
                }
            } catch (e: Exception) {
                Log.e("AllMoviesViewModel", "Error: ${e.message}", e)
                _error.postValue(e.message ?: "Unknown error occurred")
            } finally {
                _isLoading.postValue(false)
                isLoadingMore = false
            }
        }
    }
}

