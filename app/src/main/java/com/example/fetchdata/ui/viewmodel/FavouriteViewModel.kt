package com.example.fetchdata.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchdata.data.api.model.FavouriteMovie
import com.example.fetchdata.data.api.model.MovieSearch
import com.example.fetchdata.data.api.repository.IFavouriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val repository: IFavouriteRepository
) : ViewModel() {


    private val _favourites = MutableLiveData<List<FavouriteMovie>>()
    val allFavourites: LiveData<List<FavouriteMovie>> = _favourites

    private val _searchResults = MutableLiveData<List<FavouriteMovie>>()
    val searchResults: LiveData<List<FavouriteMovie>> = _searchResults

    private var currentUserEmail: String? = null
    private var favouritesCollectionJob: Job? = null


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

    /**
     * Transform FavouriteMovie to MovieSearch for display
     */
    fun transformToMovieSearch(favourites: List<FavouriteMovie>): List<MovieSearch> {
        return favourites.map { fav ->
            MovieSearch(
                title = fav.title,
                year = fav.year,
                imdbID = fav.imdbID,
                type = fav.type,
                poster = fav.poster
            )
        }
    }

    /**
     * Check if favourites list is empty
     */
    fun isEmpty(): Boolean {
        return _favourites.value.isNullOrEmpty()
    }

    /**
     * Get the count of favourites
     */
    fun getFavouriteCount(): Int {
        return _favourites.value?.size ?: 0
    }
}