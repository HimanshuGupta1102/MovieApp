package com.example.fetchdata.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fetchdata.data.model.FavouriteMovie
import com.example.fetchdata.data.model.MovieCache

@Database(entities = [FavouriteMovie::class, User::class, MovieCache::class], version = 4, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun favouriteMovieDao(): FavouriteMovieDao
    abstract fun userDao(): UserDao
    abstract fun movieCacheDao(): MovieCacheDao

    companion object {
        private const val TAG = "MovieDatabase"

        @Volatile
        private var INSTANCE: MovieDatabase? = null

        fun getDatabase(context: Context): MovieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "movie_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d(TAG, "Database created successfully")
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Log.d(TAG, "Database opened successfully")
                        }
                    })
                    .build()
                INSTANCE = instance
                Log.d(TAG, "Database instance initialized at: ${context.applicationContext.getDatabasePath("movie_database").absolutePath}")
                instance
            }
        }
    }
}
