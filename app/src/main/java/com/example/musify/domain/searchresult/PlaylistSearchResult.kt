package com.example.musify.domain.searchresult

/**
 * A class that models the result of a search operation for a
 * specific playlist.
 */
data class PlaylistSearchResult(
    val id: String,
    val name: String,
    val imageUrlString: String
)
