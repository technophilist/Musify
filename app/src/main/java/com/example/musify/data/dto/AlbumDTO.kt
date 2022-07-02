package com.example.musify.data.dto

import com.google.gson.annotations.SerializedName

/**
 * A DTO object that contains information about a specific album.
 */
data class AlbumDTO(
    val id: String,
    val name: String,
    @SerializedName("albumType") val albumType: String, // album,single or compilation
    val artists: List<ArtistInfoDTO>,
    val images: List<ImageDTO>,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("release_date_precision") val releaseDatePrecision: String, // year, month or day
    @SerializedName("total_tracks") val totalTracks: Int,
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
