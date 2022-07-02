package com.example.musify.data.dto

import com.google.gson.annotations.SerializedName

/**
 * A DTO object that contains a list of [TrackDTOWithAlbumMetadata].
 */
data class TracksWithAlbumMetadataListDTO(@SerializedName("tracks") val value: List<TrackDTOWithAlbumMetadata>)