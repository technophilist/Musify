package com.example.musify.data.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A DTO that contains the results of a search operation.
 * All the properties are nullable because a search operation
 * for just [tracks],[albums],[artists] or [playlists], or
 * a combination of any of the above can be made, in which
 * case, the other properties will be null.
 */
data class SearchResultsDTO(
    val tracks: Tracks?,
    val albums: Albums?,
    val artists: Artists?,
    val playlists: Playlists?
) {
    data class Tracks(@JsonProperty("items") val value: List<TrackDTOWithAlbumMetadata>)
    data class Albums(@JsonProperty("items") val value: List<AlbumMetadataDTO>)
    data class Artists(@JsonProperty("items") val value: List<ArtistDTO>)
    data class Playlists(@JsonProperty("items") val value: List<PlaylistMetadataDTO>)
}
