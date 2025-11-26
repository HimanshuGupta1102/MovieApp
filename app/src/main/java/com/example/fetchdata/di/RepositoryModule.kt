package com.example.fetchdata.di

import com.example.fetchdata.data.api.repository.IFavouriteRepository
import com.example.fetchdata.data.api.repository.IMovieRepository
import com.example.fetchdata.data.api.repository.IUserRepository
import com.example.fetchdata.data.impl.repository.FavouriteRepositoryImpl
import com.example.fetchdata.data.impl.repository.MovieRepositoryImpl
import com.example.fetchdata.data.impl.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepository: MovieRepositoryImpl
    ): IMovieRepository

    @Binds
    @Singleton
    abstract fun bindFavouriteRepository(
        favouriteRepository: FavouriteRepositoryImpl
    ): IFavouriteRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepository: UserRepositoryImpl
    ): IUserRepository
}