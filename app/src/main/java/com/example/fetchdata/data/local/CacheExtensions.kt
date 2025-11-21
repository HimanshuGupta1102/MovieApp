package com.example.fetchdata.data.local
import com.example.fetchdata.data.model.MovieCache
import com.example.fetchdata.data.model.MovieSearch

fun MovieCache.toMovieSearch(): MovieSearch {
    return MovieSearch(
        Title = this.title,
        Year = this.year,
        imdbID = this.imdbID,
        Type = this.type,
        Poster = this.poster
    )
}

fun MovieSearch.toMovieCache(query: String, page: Int): MovieCache {
    return MovieCache(
        imdbID = this.imdbID,
        title = this.Title,
        year = this.Year,
        poster = this.Poster,
        type = this.Type,
        searchQuery = query,
        page = page
    )
}

fun List<MovieCache>.toMovieSearchList(): List<MovieSearch> {
    return this.map { it.toMovieSearch() }
}

fun List<MovieSearch>.toMovieCacheList(query: String, page: Int): List<MovieCache> {
    return this.map { it.toMovieCache(query, page) }
}

