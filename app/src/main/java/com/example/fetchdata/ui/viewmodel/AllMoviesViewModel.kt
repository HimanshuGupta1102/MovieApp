package com.example.fetchdata.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchdata.data.api.model.MovieSearch
import com.example.fetchdata.data.api.repository.IMovieRepository
import com.example.fetchdata.data.impl.repository.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllMoviesViewModel @Inject constructor(
    private val repository: IMovieRepository
) : ViewModel() {

    private val _movies = MutableLiveData<List<MovieSearch>>()
    val movies: LiveData<List<MovieSearch>> = _movies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _canLoadMore = MutableLiveData<Boolean>(true)
    val canLoadMore: LiveData<Boolean> = _canLoadMore

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

        if (query == currentQuery && currentPage == 1 && allMovies.isNotEmpty()) {
            Log.d("AllMoviesViewModel", "Skipping duplicate search for: $query (already have results)")
            return
        }

        Log.d("AllMoviesViewModel", "Starting new search for: '$query'")
        currentQuery = query
        currentPage = 1
        totalResults = 0
        allMovies.clear()
        _movies.postValue(emptyList()) // Clear UI immediately
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

                Log.d("AllMoviesViewModel", "Fetching movies for query: '$currentQuery', page: $currentPage")

                // Use cache-first strategy
                val result = repository.searchMoviesWithCache(currentQuery, currentPage)

                when (result) {
                    is Resource.Success -> {
                        val newMovies = result.data ?: emptyList()
                        Log.d("AllMoviesViewModel", "Success: Got ${newMovies.size} movies")

                        if (currentPage == 1) {
                            allMovies.clear()
                        }

                        allMovies.addAll(newMovies)
                        _movies.postValue(allMovies.toList())

                        Log.d("AllMoviesViewModel", "Added ${newMovies.size} movies. Total: ${allMovies.size}")

                        if (newMovies.isEmpty() && currentPage > 1) {
                            _error.postValue("No more results available")
                        }
                    }

                    is Resource.Error -> {
                        Log.e("AllMoviesViewModel", "Error: ${result.message}")

                        // If we have cached data despite the error, use it
                        if (result.data != null) {
                            val cachedMovies = result.data
                            if (currentPage == 1) {
                                allMovies.clear()
                            }
                            allMovies.addAll(cachedMovies)
                            _movies.postValue(allMovies.toList())
                            Log.d("AllMoviesViewModel", "Using cached data: ${cachedMovies.size} movies")
                        }

                        // Show user-friendly error message
                        val errorMsg = when {
                            result.message?.contains("No internet") == true -> result.message
                            result.message?.contains("Unable to resolve host") == true ->
                                "No internet connection. Showing cached results."
                            result.message?.contains("timeout") == true ->
                                "Request timed out. Please try again."
                            result.message?.contains("No movies found") == true -> result.message
                            else -> result.message ?: "Error loading movies"
                        }
                        _error.postValue(errorMsg)

                        if (currentPage > 1 && result.data == null) {
                            currentPage-- // Revert page increment on error without data
                        }
                    }

                    is Resource.Loading -> {
                    }
                }
            } catch (e: Exception) {
                Log.e("AllMoviesViewModel", "Unexpected error: ${e.message}", e)
                _error.postValue("Error loading movies: ${e.message}")

                if (currentPage > 1) {
                    currentPage-- // Revert page increment on error
                }
            } finally {
                _isLoading.postValue(false)
                isLoadingMore = false
                updateLoadMoreState()
            }
        }
    }

    fun formatErrorMessage(message: String?): String {
        return when {
            message == null -> "Unknown error"
            message.contains("No internet", ignoreCase = true) -> message
            message.contains("Unable to resolve host", ignoreCase = true) ->
                "No internet connection. Please check your network."
            message.contains("timeout", ignoreCase = true) ->
                "Request timed out. Please try again."
            message.contains("No movies found", ignoreCase = true) ->
                "No movies found. Try a different search."
            message.contains("No more results available", ignoreCase = true) -> message
            else -> "Error: $message"
        }
    }

    fun shouldShowErrorAsToast(error: String?, hasMovies: Boolean): Boolean {
        if (error == null) return false

        return when {
            error.contains("No more results available", ignoreCase = true) -> true
            error.contains("No internet", ignoreCase = true) && hasMovies -> true
            error.contains("cached", ignoreCase = true) && hasMovies -> true
            else -> false
        }
    }

    private fun updateLoadMoreState() {
        _canLoadMore.postValue(
            allMovies.size < totalResults || totalResults == 0
        )
    }

    fun clearExpiredCache() {
        viewModelScope.launch {
            repository.clearExpiredCache()
        }
    }

    fun refreshMovies() {
        viewModelScope.launch {
            repository.clearAllCache()
            searchMovies(currentQuery)
        }
    }
}
