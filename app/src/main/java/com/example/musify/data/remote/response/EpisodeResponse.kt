package com.example.musify.data.remote.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A response object that contains information about a specific episode.
 */
data class EpisodeResponse(
    val id: String,
    @JsonProperty("name") val title: String,
    val description: String,
    @JsonProperty("duration_ms") val durationMillis: Long,
    @JsonProperty("release_date") val releaseDate: String,
    @JsonProperty("audio_preview_url") val previewUrl: String?,
    val show: EpisodeShowResponse
) {
    data class EpisodeShowResponse(
        val id: String,
        val name: String,
        val images: List<ImageResponse>
    )
}
