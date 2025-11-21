package com.example.fetchdata.data.repository

import android.util.Log
import com.example.fetchdata.data.local.MovieCacheDao
import com.example.fetchdata.data.local.toMovieCacheList
import com.example.fetchdata.data.local.toMovieSearchList
import com.example.fetchdata.data.model.MovieDetail
import com.example.fetchdata.data.model.MovieSearch
import com.example.fetchdata.data.remote.RetrofitInstance
import com.example.fetchdata.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository(
    private val movieCacheDao: MovieCacheDao
) {
    companion object {
        private const val TAG = "MovieRepository"
        private const val CACHE_EXPIRATION_TIME = 24 * 60 * 60 * 1000L // 24 hours in milliseconds
    }

    suspend fun searchMoviesWithCache(searchQuery: String, page: Int): Resource<List<MovieSearch>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Searching movies: query='$searchQuery', page=$page")

                // Step 1: Check cache first
                val cachedMovies = movieCacheDao.getCachedMovies(searchQuery, page)
                val isCacheValid = cachedMovies.isNotEmpty() && isCacheValid(cachedMovies.first().cachedAt)

                if (isCacheValid) {
                    Log.d(TAG, "Returning ${cachedMovies.size} movies from cache")
                    return@withContext Resource.Success(cachedMovies.toMovieSearchList())
                }

                // Step 2: Fetch from network
                Log.d(TAG, "Cache invalid/missing, fetching from API")
                val response = RetrofitInstance.api.searchMovies(Constants.API_KEY, searchQuery, page)

                if (response.Response == "True") {
                    val movies = response.Search ?: emptyList()

                    // Step 3: Cache the results
                    if (movies.isNotEmpty()) {
                        cacheMovies(movies, searchQuery, page)
                        Log.d(TAG, "Cached ${movies.size} movies from API")
                    }

                    return@withContext Resource.Success(movies)
                } else {
                    // API returned error, fallback to cache if available
                    if (cachedMovies.isNotEmpty()) {
                        Log.d(TAG, "API error, returning expired cache as fallback")
                        return@withContext Resource.Success(cachedMovies.toMovieSearchList())
                    }
                    return@withContext Resource.Error("No movies found")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error searching movies: ${e.message}", e)

                // Network error: return cached data if available (even if expired)
                val cachedMovies = movieCacheDao.getCachedMovies(searchQuery, page)
                if (cachedMovies.isNotEmpty()) {
                    Log.d(TAG, "Network error, returning cached data as fallback")
                    return@withContext Resource.Error(
                        message = "No internet. Showing cached results.",
                        data = cachedMovies.toMovieSearchList()
                    )
                }

                return@withContext Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    /**
     * Legacy method for backward compatibility - directly calls API
     */
    suspend fun searchMovies(searchQuery: String, page: Int) =
        RetrofitInstance.api.searchMovies(Constants.API_KEY, searchQuery, page)

    suspend fun getMovieDetails(apiKey: String, imdbId: String): MovieDetail {
        return RetrofitInstance.api.getMovieDetail(Constants.API_KEY, imdbId)
    }

    /**
     * Cache movies to database
     */
    private suspend fun cacheMovies(movies: List<MovieSearch>, query: String, page: Int) {
        try {
            val movieCaches = movies.toMovieCacheList(query, page)
            movieCacheDao.insertMovies(movieCaches)
        } catch (e: Exception) {
            Log.e(TAG, "Error caching movies: ${e.message}", e)
        }
    }

    /**
     * Check if cache is still valid (< 24 hours old)
     */
    private fun isCacheValid(cachedTime: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val age = currentTime - cachedTime
        return age < CACHE_EXPIRATION_TIME
    }

    /**
     * Clear expired cache entries
     */
    suspend fun clearExpiredCache() {
        try {
            val expirationTime = System.currentTimeMillis() - CACHE_EXPIRATION_TIME
            movieCacheDao.deleteExpiredCache(expirationTime)
            Log.d(TAG, "Cleared expired cache")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing expired cache: ${e.message}", e)
        }
    }

    /**
     * Clear all cache
     */
    suspend fun clearAllCache() {
        try {
            movieCacheDao.clearAllCache()
            Log.d(TAG, "Cleared all cache")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing cache: ${e.message}", e)
        }
    }

    /**
     * Get cache statistics
     */
    suspend fun getCacheCount(): Int {
        return try {
            movieCacheDao.getCacheCount()
        } catch (e: Exception) {
            0
        }
    }
}