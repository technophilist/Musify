package com.example.musify.data.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A DTO that contains information related to a specific Playlist.
 */
data class PlaylistDTO(
    val id: String,
    val name: String,
    val images: List<ImageDTO>,
    @JsonProperty("owner") val ownerName: OwnerNameWrapper,
    @JsonProperty("followers") val numberOfFollowers: NumberOfFollowersWrapper,
    val tracks: Tracks
) {
    /**
     * A class that wraps a string that contains the name of the owner
     * associated with a playlist.
     */
    data class OwnerNameWrapper(@JsonProperty("display_name") val value: String)

    /**
     * A class that wraps a string that contains the number of followers
     * of a particular playlist.
     */
    data class NumberOfFollowersWrapper(@JsonProperty("total") val value: String)

    /**
     * A class that contains a list of [items] of type [TrackDTOWithAlbumMetadataWrapper].
     */
    data class Tracks(val items: List<TrackDTOWithAlbumMetadataWrapper>)

    /**
     * A wrapper class that wraps an instance of [TrackDTOWithAlbumMetadata]
     */
    data class TrackDTOWithAlbumMetadataWrapper(@JsonProperty("track") val track: TrackDTOWithAlbumMetadata)
}
