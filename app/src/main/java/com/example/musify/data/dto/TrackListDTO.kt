package com.example.musify.data.dto

import com.google.gson.annotations.SerializedName

/**
 * A DTO object that contains a list of tracks.
 */
@JvmInline
value class TrackListDTO(@SerializedName("tracks") val value: List<TrackDTOWithAlbumMetadata>)