package com.example.musify.data.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A DTO object that contains a list of [TrackResponseWithAlbumMetadata].
 */
data class TracksWithAlbumMetadataListResponse(@JsonProperty("tracks") val value: List<TrackResponseWithAlbumMetadata>)