package com.example.musify.domain.searchresult

/**
 * A class that models the result of a search operation for a
 * specific album.
 * Note: The [artistsString] property is meant to hold a comma separated
 * list of artists who worked on the album.
 */
class AlbumSearchResult(
    val id: String,
    val name: String,
    val artistsString: String,
    val albumArtUrlString: String,
    val yearOfReleaseString: String,
)
