package com.example.fetchdata.data.impl.local.mapper

import com.example.fetchdata.data.api.model.MovieCache
import com.example.fetchdata.data.api.model.MovieSearch

fun MovieCache.toMovieSearch(): MovieSearch {
    return MovieSearch(
        title = this.title,
        year = this.year,
        imdbID = this.imdbID,
        type = this.type,
        poster = this.poster
    )
}

fun MovieSearch.toMovieCache(query: String, page: Int): MovieCache {
    return MovieCache(
        imdbID = this.imdbID,
        title = this.title,
        year = this.year,
        poster = this.poster,
        type = this.type,
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