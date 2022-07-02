package com.example.musify.data.dto

import com.google.gson.annotations.SerializedName

/**
 * A DTO object that represents an album. It also contains additional
 * meta data about the album and includes information about the
 * artists.
 */
data class AlbumDTO(
    val id: String,
    val name: String,
    @SerializedName("albumType") val albumType: String, // album,single or compilation
    val artists: List<ArtistDTO>,
    val images: List<ImageDTO>,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("release_date_precision") val releaseDatePrecision: String, // year, month or day
    @SerializedName("total_tracks") val totalTracks: Int,
    val tracks: TracksWithoutAlbumMetadataList
) {
    /**
     * A data class that contains the list of tracks associated with
     * a particular [AlbumDTO].
     */
    data class TracksWithoutAlbumMetadataList(@SerializedName("items") val value: List<TrackDTOWithoutAlbumMetadata>)

    /**
     * A DTO object that contains information about a specific track
     * without containing metadata about the album.
     * [TrackDTOWithAlbumMetadata] contains both, information about
     * the track and the metadata about the associated album.
     */
    data class TrackDTOWithoutAlbumMetadata(
        val id: String,
        val name: String,
        @SerializedName("preview_url") val previewUrl: String?,
        @SerializedName("is_playable") val isPlayable: Boolean,
        val explicit: Boolean,
        @SerializedName("duration_ms") val durationInMillis: Int
    )
}
