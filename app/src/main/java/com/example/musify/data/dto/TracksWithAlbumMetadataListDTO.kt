package com.example.musify.data.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A DTO object that contains a list of [TrackDTOWithAlbumMetadata].
 */
data class TracksWithAlbumMetadataListDTO(@JsonProperty("tracks") val value: List<TrackDTOWithAlbumMetadata>)