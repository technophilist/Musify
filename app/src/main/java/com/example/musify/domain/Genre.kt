package com.example.musify.domain

/**
 * A class that models a specific genre.
 * @param id unique id of the genre
 * @param name the name of the genre
 */
data class Genre(
    val id: String,
    val name: String,
    val genreType: GenreType
) {
    enum class GenreType {
        AMBIENT,
        CHILL,
        CLASSICAL,
        DANCE,
        ELECTRONIC,
        METAL,
        RAINY_DAY,
        ROCK,
        PIANO,
        POP
    }
}
