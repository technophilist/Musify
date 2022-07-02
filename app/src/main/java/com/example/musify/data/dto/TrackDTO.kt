package com.example.musify.data.dto

/**
 * A DTO object that contains information about a specific track.
 */
data class TrackDTO(
    val id: String,
    val name: String,
    val preview_url: String?,
    val is_playable: Boolean,
    val explicit: Boolean,
    val duration_ms: Int,
    val album: AlbumMetadataDTO
)