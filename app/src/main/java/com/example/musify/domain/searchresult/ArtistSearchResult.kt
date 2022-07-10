package com.example.musify.domain.searchresult

/**
 * A class that models the result of a search operation for a
 * specific artist.
 */
data class ArtistSearchResult(
    val id: String,
    val name: String,
    val imageUrlString: String
)
