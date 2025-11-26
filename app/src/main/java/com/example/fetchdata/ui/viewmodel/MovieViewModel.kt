package com.example.fetchdata.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchdata.data.api.model.FavouriteMovie
import com.example.fetchdata.data.api.model.MovieDetail
import com.example.fetchdata.data.api.repository.IMovieRepository
import com.example.fetchdata.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: IMovieRepository
) : ViewModel() {

    private val _movieDetail = MutableLiveData<MovieDetail>()
    val movieDetail: LiveData<MovieDetail> = _movieDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _uiEvent = MutableLiveData<UiEvent>()
    val uiEvent: LiveData<UiEvent> = _uiEvent

    sealed class UiEvent {
        data class ShowMessage(val message: String) : UiEvent()
        data class ShowError(val error: String) : UiEvent()
    }

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

    fun createFavouriteMovie(movie: MovieDetail, userEmail: String): FavouriteMovie {
        return FavouriteMovie(
            imdbID = movie.imdbID,
            userEmail = userEmail,
            title = movie.title,
            year = movie.year,
            poster = movie.poster ?: "",
            type = movie.type
        )
    }

    fun canModifyFavourites(userEmail: String?): Boolean {
        if (userEmail == null) {
            _uiEvent.value = UiEvent.ShowError("Please sign in to add favourites")
            return false
        }
        return true
    }

    fun onFavouriteAdded() {
        _uiEvent.value = UiEvent.ShowMessage("Added to favorites")
    }

    fun onFavouriteRemoved() {
        _uiEvent.value = UiEvent.ShowMessage("Removed from favorites")
    }
}