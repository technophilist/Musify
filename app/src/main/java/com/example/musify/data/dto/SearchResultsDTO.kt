package com.example.musify.data.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A DTO that contains the results of a search operation.
 */
data class SearchResultsDTO(
    val tracks: Tracks,
    val albums: Albums,
    val artists: Artists,
    val playlists: Playlists
) {
    data class Tracks(@JsonProperty("items") val value: List<TrackDTOWithAlbumMetadata>)
    data class Albums(@JsonProperty("items") val value: List<AlbumMetadataDTO>)
    data class Artists(@JsonProperty("items") val value: List<ArtistDTO>)
    data class Playlists(@JsonProperty("items") val value: List<PlaylistMetadataDTO>)
}
