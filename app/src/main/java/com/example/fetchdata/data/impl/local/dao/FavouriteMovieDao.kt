package com.example.fetchdata.data.impl.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fetchdata.data.api.model.FavouriteMovie
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteMovieDao {

    @Query("SELECT * FROM favourite_movies WHERE userEmail = :userEmail ORDER BY addedAt DESC")
    fun getFavouritesForUser(userEmail: String): Flow<List<FavouriteMovie>>
    @Query("SELECT * FROM favourite_movies WHERE imdbID = :imdbId AND userEmail = :userEmail LIMIT 1")
    suspend fun getFavouriteById(imdbId: String, userEmail: String): FavouriteMovie?

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_movies WHERE imdbID = :imdbId AND userEmail = :userEmail)")
    suspend fun isFavourite(imdbId: String, userEmail: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavourite(movie: FavouriteMovie)

    @Query("DELETE FROM favourite_movies WHERE imdbID = :imdbId AND userEmail = :userEmail")
    suspend fun removeFavourite(imdbId: String, userEmail: String)

    @Query("SELECT * FROM favourite_movies WHERE userEmail = :userEmail AND title LIKE '%' || :query || '%' ORDER BY addedAt DESC")
    fun searchFavourites(query: String, userEmail: String): Flow<List<FavouriteMovie>>
}

