package com.example.musify.data.repositories.genresrepository

import com.example.musify.domain.Genre

interface GenresRepository {
    fun fetchAvailableGenres(): List<Genre>
}