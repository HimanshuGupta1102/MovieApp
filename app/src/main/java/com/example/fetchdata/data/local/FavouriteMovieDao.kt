package com.example.fetchdata.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteMovieDao {

    @Query("SELECT * FROM favourite_movies ORDER BY addedAt DESC")
    fun getAllFavourites(): Flow<List<FavouriteMovie>>

    @Query("SELECT * FROM favourite_movies WHERE imdbID = :imdbId")
    suspend fun getFavouriteById(imdbId: String): FavouriteMovie?

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_movies WHERE imdbID = :imdbId)")
    suspend fun isFavourite(imdbId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavourite(movie: FavouriteMovie)

    @Query("DELETE FROM favourite_movies WHERE imdbID = :imdbId")
    suspend fun removeFavourite(imdbId: String)

    @Query("SELECT * FROM favourite_movies WHERE title LIKE '%' || :query || '%' ORDER BY addedAt DESC")
    fun searchFavourites(query: String): Flow<List<FavouriteMovie>>
}

