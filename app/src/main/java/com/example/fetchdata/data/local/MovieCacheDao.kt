package com.example.fetchdata.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fetchdata.data.model.MovieCache

@Dao
interface MovieCacheDao {

    @Query("SELECT * FROM movie_cache WHERE searchQuery = :query AND page = :page ORDER BY cachedAt DESC")
    suspend fun getCachedMovies(query: String, page: Int): List<MovieCache>

    @Query("SELECT * FROM movie_cache WHERE imdbID = :imdbId LIMIT 1")
    suspend fun getCachedMovieById(imdbId: String): MovieCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieCache>)

    @Query("DELETE FROM movie_cache WHERE cachedAt < :expirationTime")
    suspend fun deleteExpiredCache(expirationTime: Long)

    @Query("DELETE FROM movie_cache WHERE searchQuery = :query")
    suspend fun deleteCacheForQuery(query: String)

    @Query("DELETE FROM movie_cache")
    suspend fun clearAllCache()

    @Query("SELECT COUNT(*) FROM movie_cache")
    suspend fun getCacheCount(): Int
}

