package com.example.musify.data.remote.response

import com.example.musify.data.utils.getImageDtoForImageSize
import com.example.musify.domain.MusicSummary
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL

/**
 * A response that contains information related to a specific Playlist.
 */
data class PlaylistResponse(
    val id: String,
    val name: String,
    val images: List<ImageResponse>,
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
     * A class that contains a list of [items] of type [TrackResponseWithAlbumMetadataWrapper].
     */
    data class Tracks(val items: List<TrackResponseWithAlbumMetadataWrapper>)

    /**
     * A wrapper class that wraps an instance of [TrackResponseWithAlbumMetadata]
     */
    data class TrackResponseWithAlbumMetadataWrapper(@JsonProperty("track") val track: TrackResponseWithAlbumMetadata)
}

/**
 * A mapper function used to map an instance of [PlaylistResponse] to
 * an instance of [MusicSummary.PlaylistSummary].
 *
 * Note:[getImageDtoForImageSize] cannot be used because playlists usually
 * contain only a single image. Therefore, the url of the first image
 * is mapped to [MusicSummary.PlaylistSummary.associatedImageUrl].
 */
fun PlaylistResponse.toPlayListSummary() = MusicSummary.PlaylistSummary(
    id = id,
    name = name,
    associatedImageUrl = URL(images.first().url)
)
