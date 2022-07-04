package com.example.musify.data.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A DTO object that contains metadata about a specific album.
 * Note: The object only contains metadata. It doesn't contain
 * the track list. [AlbumDTO] contains the track list in addition
 * to the metadata.
 */
data class AlbumMetadataDTO(
    val id: String,
    val name: String,
    @JsonProperty("album_type") val albumType: String, // album,single or compilation
    val artists: List<ArtistInfoDTO>,
    val images: List<ImageDTO>,
    @JsonProperty("release_date") val releaseDate: String,
    @JsonProperty("release_date_precision") val releaseDatePrecision: String, // year, month or day
    @JsonProperty("total_tracks") val totalTracks: Int,
    val type: String
) {
    /**
     * A DTO object associated with [AlbumMetadataDTO] that contains information
     * about an artist.
     */
    data class ArtistInfoDTO(
        val id: String,
        val name: String
    )
}
