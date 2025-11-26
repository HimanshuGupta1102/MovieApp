package com.example.fetchdata.data.impl.repository

import com.example.fetchdata.data.api.model.FavouriteMovie
import com.example.fetchdata.data.api.repository.IFavouriteRepository
import com.example.fetchdata.data.impl.local.dao.FavouriteMovieDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of IFavouriteRepository.
 * Handles favourite movies operations for users.
 */
@Singleton
class FavouriteRepositoryImpl @Inject constructor(
    private val dao: FavouriteMovieDao
) : IFavouriteRepository {

    override fun getFavouritesForUser(email: String): Flow<List<FavouriteMovie>> {
        return dao.getFavouritesForUser(email)
    }

    override suspend fun isFavourite(imdbId: String, email: String): Boolean {
        return dao.isFavourite(imdbId, email)
    }

    override suspend fun addFavourite(movie: FavouriteMovie) {
        dao.addFavourite(movie)
    }

    override suspend fun removeFavourite(imdbId: String, email: String) {
        dao.removeFavourite(imdbId, email)
    }

    override fun searchFavourites(query: String, email: String): Flow<List<FavouriteMovie>> {
        return dao.searchFavourites(query, email)
    }
}

