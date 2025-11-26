package com.example.fetchdata.data.api.repository

import com.example.fetchdata.data.api.model.FavouriteMovie
import kotlinx.coroutines.flow.Flow

interface IFavouriteRepository {

    fun getFavouritesForUser(email: String): Flow<List<FavouriteMovie>>

    suspend fun isFavourite(imdbId: String, email: String): Boolean

    suspend fun addFavourite(movie: FavouriteMovie)

    suspend fun removeFavourite(imdbId: String, email: String)

    fun searchFavourites(query: String, email: String): Flow<List<FavouriteMovie>>
}