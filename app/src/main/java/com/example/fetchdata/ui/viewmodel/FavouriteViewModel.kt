package com.example.fetchdata.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.fetchdata.data.local.FavouriteMovie
import com.example.fetchdata.data.local.MovieDatabase
import com.example.fetchdata.data.repository.FavouriteRepository
import kotlinx.coroutines.launch

class FavouriteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FavouriteRepository
    val allFavourites: LiveData<List<FavouriteMovie>>

    private val _searchResults = MutableLiveData<List<FavouriteMovie>>()
    val searchResults: LiveData<List<FavouriteMovie>> = _searchResults

    init {
        val dao = MovieDatabase.getDatabase(application).favouriteMovieDao()
        repository = FavouriteRepository(dao)
        allFavourites = repository.getAllFavourites().asLiveData()
    }

    fun addFavourite(movie: FavouriteMovie) = viewModelScope.launch {
        repository.addFavourite(movie)
    }

    fun removeFavourite(imdbId: String) = viewModelScope.launch {
        repository.removeFavourite(imdbId)
    }

    fun isFavourite(imdbId: String, callback: (Boolean) -> Unit) = viewModelScope.launch {
        val isFav = repository.isFavourite(imdbId)
        callback(isFav)
    }

    fun searchFavourites(query: String) = viewModelScope.launch {
        repository.searchFavourites(query).asLiveData().observeForever { results ->
            _searchResults.postValue(results)
        }
    }
}

