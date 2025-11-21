package com.example.fetchdata.data.repository

import com.example.fetchdata.data.model.FavouriteMovie
import com.example.fetchdata.data.local.FavouriteMovieDao
import kotlinx.coroutines.flow.Flow

class FavouriteRepository(private val dao: FavouriteMovieDao) {

    fun getFavouritesForUser(email: String): Flow<List<FavouriteMovie>> = dao.getFavouritesForUser(email)

    suspend fun isFavourite(imdbId: String, email: String): Boolean = dao.isFavourite(imdbId, email)

    suspend fun addFavourite(movie: FavouriteMovie) = dao.addFavourite(movie)

    suspend fun removeFavourite(imdbId: String, email: String) = dao.removeFavourite(imdbId, email)

    fun searchFavourites(query: String, email: String): Flow<List<FavouriteMovie>> = dao.searchFavourites(query, email)
}
