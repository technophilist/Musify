package com.example.musify.domain.searchresult

/**
 * A class that models the result of a search operation for a
 * specific track.
 * Note: The [artistsString] property is meant to hold a comma separated
 * list of artists who worked on the track.
 */
data class TrackSearchResult(
    val id: String,
    val name: String,
    val imageUrlString: String,
    val artistsString: String,
    val trackUrlString: String
)
