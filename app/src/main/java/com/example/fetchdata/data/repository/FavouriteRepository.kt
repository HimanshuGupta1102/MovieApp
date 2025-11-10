package com.example.fetchdata.data.repository

import com.example.fetchdata.data.local.FavouriteMovie
import com.example.fetchdata.data.local.FavouriteMovieDao
import kotlinx.coroutines.flow.Flow

class FavouriteRepository(private val dao: FavouriteMovieDao) {

    fun getAllFavourites(): Flow<List<FavouriteMovie>> = dao.getAllFavourites()

    suspend fun isFavourite(imdbId: String): Boolean = dao.isFavourite(imdbId)

    suspend fun addFavourite(movie: FavouriteMovie) = dao.addFavourite(movie)

    suspend fun removeFavourite(imdbId: String) = dao.removeFavourite(imdbId)

    fun searchFavourites(query: String): Flow<List<FavouriteMovie>> = dao.searchFavourites(query)
}

