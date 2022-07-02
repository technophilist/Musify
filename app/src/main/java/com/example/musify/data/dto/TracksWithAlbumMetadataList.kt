package com.example.musify.data.dto

import com.google.gson.annotations.SerializedName

/**
 * A DTO object that contains a list of [TrackDTOWithAlbumMetadata].
 */
data class TracksWithAlbumMetadataList(@SerializedName("tracks") val value: List<TrackDTOWithAlbumMetadata>)