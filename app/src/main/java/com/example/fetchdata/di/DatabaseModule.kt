package com.example.fetchdata.di

import android.content.Context
import com.example.fetchdata.data.impl.local.dao.FavouriteMovieDao
import com.example.fetchdata.data.impl.local.dao.MovieCacheDao
import com.example.fetchdata.data.impl.local.dao.UserDao
import com.example.fetchdata.data.impl.local.database.MovieDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMovieDatabase(@ApplicationContext context: Context): MovieDatabase {
        return MovieDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideMovieCacheDao(database: MovieDatabase): MovieCacheDao {
        return database.movieCacheDao()
    }

    @Provides
    @Singleton
    fun provideFavouriteMovieDao(database: MovieDatabase): FavouriteMovieDao {
        return database.favouriteMovieDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: MovieDatabase): UserDao {
        return database.userDao()
    }
}