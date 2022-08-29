package com.example.musify.data.remote.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A response class that contains a list of tracks in a particular
 * playlist.
 */
data class PlaylistItemsResponse(val items: List<TrackResponseWithAlbumMetadataWrapper>) {
    data class TrackResponseWithAlbumMetadataWrapper(@JsonProperty("track") val track: TrackResponseWithAlbumMetadata)
}

