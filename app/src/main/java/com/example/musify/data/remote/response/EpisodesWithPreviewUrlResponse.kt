package com.example.musify.data.remote.response

import com.example.musify.data.remote.response.EpisodesWithPreviewUrlResponse.EpisodeMetadataResponseWithPreviewUrl
import com.fasterxml.jackson.annotation.JsonProperty


/**
 * A response object that contains a list of [EpisodeMetadataResponseWithPreviewUrl].
 */
data class EpisodesWithPreviewUrlResponse(val items: List<EpisodeMetadataResponseWithPreviewUrl>) {
    /**
     * A response object that contains metadata of a specific episode together
     * with a [previewUrl]. It is essentially the same as [EpisodeMetadataResponse]
     * with a field for [previewUrl].
     */
    data class EpisodeMetadataResponseWithPreviewUrl(
        val id: String,
        @JsonProperty("name") val title: String,
        val description: String,
        @JsonProperty("duration_ms") val durationMillis: Long,
        val images: List<ImageResponse>,
        @JsonProperty("release_date") val releaseDate: String,
        @JsonProperty("preview_url") val previewUrl: String?
    )
}
