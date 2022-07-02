package com.example.musify.data.dto

import com.google.gson.annotations.SerializedName

/**
 * A DTO that contains information related to a specific Playlist.
 */
data class PlaylistDTO(
    val id: String,
    val name: String,
    val images: List<ImageDTO>,
    @SerializedName("owner") val ownerName: OwnerNameWrapper,
    @SerializedName("followers") val numberOfFollowers: NumberOfFollowersWrapper,
    val tracks: Tracks
) {
    /**
     * A class that wraps a string that contains the name of the owner
     * associated with a playlist.
     */
    data class OwnerNameWrapper(@SerializedName("display_name") val value: String)

    /**
     * A class that wraps a string that contains the number of followers
     * of a particular playlist.
     */
    data class NumberOfFollowersWrapper(@SerializedName("total") val value: String)

    /**
     * A DTO object that contains a list of [items] of type [TrackDTOWithAlbumMetadataWrapper].
     */
    data class Tracks(val items: List<TrackDTOWithAlbumMetadataWrapper>)

    /**
     * A wrapper class that wraps an instance of [TrackDTOWithAlbumMetadata]
     */
    data class TrackDTOWithAlbumMetadataWrapper(@SerializedName("track") val track: TrackDTOWithAlbumMetadata)
}
