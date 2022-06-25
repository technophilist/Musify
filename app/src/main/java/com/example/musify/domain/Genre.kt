package com.example.musify.domain

import java.net.URL

/**
 * A class that models a specific genre.
 * @param id unique id of the genre
 * @param name the name of the genre
 * @param coverArtUrl an instance of [URL] that specifies the url for
 * the cover art associated with the genre.
 */
data class Genre(
    val id: String,
    val name: String,
    val coverArtUrl: URL
)
