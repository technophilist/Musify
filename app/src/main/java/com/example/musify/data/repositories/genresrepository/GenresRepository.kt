package com.example.musify.data.repositories.genresrepository

import com.example.musify.domain.Genre

/**
 * A repository that contains all methods related to genres.
 */
interface GenresRepository {
    fun fetchAvailableGenres(): List<Genre>
}