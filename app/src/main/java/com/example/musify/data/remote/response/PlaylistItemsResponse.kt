package com.example.musify.data.remote.response

/**
 * A response class that contains a list of tracks in a particular
 * playlist.
 */
data class PlaylistItemsResponse(
    val items: List<PlaylistResponse.TrackResponseWithAlbumMetadataWrapper> // TODO move PlaylistResponse.TrackResponseWithAlbumMetadataWrapper into a nested class of this class
)