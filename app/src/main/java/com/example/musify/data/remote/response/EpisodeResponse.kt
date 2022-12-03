package com.example.musify.data.remote.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A response object that contains metadata of a specific episode.
 */
data class EpisodeMetadataResponse(
    val id: String,
    @JsonProperty("name") val title: String,
    val description: String,
    @JsonProperty("duration_ms") val durationMillis: Long,
    val images: List<ImageResponse>,
    @JsonProperty("release_date") val releaseDate: String
)
