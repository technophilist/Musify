package com.example.musify.data.remote.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A response object that contains a list of [TrackResponseWithAlbumMetadata].
 */
data class TracksWithAlbumMetadataListResponse(@JsonProperty("tracks") val value: List<TrackResponseWithAlbumMetadata>)