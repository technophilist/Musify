package com.example.musify.data.dto

/**
 * A DTO object that contains information about a specific album.
 */
data class AlbumDTO(
    val id: String,
    val name: String,
    val album_type: String,
    val artists: List<ArtistInfoDTO>,
    val images: List<ImageDTO>,
    val release_date: String,
    val release_date_precision: String,
    val total_tracks: Int,
    val type: String
) {
    /**
     * A DTO object associated with [AlbumDTO] that contains the artist
     * information about an artist.
     */
    data class ArtistInfoDTO(
        val id: String,
        val name: String
    )
}
