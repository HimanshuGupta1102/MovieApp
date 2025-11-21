package com.example.fetchdata.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fetchdata.data.model.FavouriteMovie
import com.example.fetchdata.data.local.MovieDatabase
import com.example.fetchdata.data.repository.FavouriteRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouriteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FavouriteRepository

    private val _favourites = MutableLiveData<List<FavouriteMovie>>()
    val allFavourites: LiveData<List<FavouriteMovie>> = _favourites

    private val _searchResults = MutableLiveData<List<FavouriteMovie>>()
    val searchResults: LiveData<List<FavouriteMovie>> = _searchResults

    private var currentUserEmail: String? = null
    private var favouritesCollectionJob: Job? = null

    init {
        val dao = MovieDatabase.getDatabase(application).favouriteMovieDao()
        repository = FavouriteRepository(dao)
    }

    fun setUserEmail(email: String?) {
        if (email == currentUserEmail) return
        currentUserEmail = email
        favouritesCollectionJob?.cancel()
        _favourites.value = emptyList()
        _searchResults.value = emptyList()

        if (email == null) {
            return // user logged out
        }

        favouritesCollectionJob = viewModelScope.launch {
            repository.getFavouritesForUser(email).collectLatest { list ->
                _favourites.postValue(list)
            }
        }
    }

    fun addFavourite(movie: FavouriteMovie) = viewModelScope.launch {
        val email = currentUserEmail ?: return@launch
        repository.addFavourite(movie.copy(userEmail = email))
    }

    fun removeFavourite(imdbId: String) = viewModelScope.launch {
        val email = currentUserEmail ?: return@launch
        repository.removeFavourite(imdbId, email)
    }

    fun isFavourite(imdbId: String, callback: (Boolean) -> Unit) = viewModelScope.launch {
        val email = currentUserEmail
        if (email == null) {
            callback(false)
            return@launch
        }
        val isFav = repository.isFavourite(imdbId, email)
        callback(isFav)
    }

    fun searchFavourites(query: String) = viewModelScope.launch {
        val email = currentUserEmail ?: return@launch
        repository.searchFavourites(query, email).collectLatest { results ->
            _searchResults.postValue(results)
        }
    }
}
