package com.example.musify.data.remote.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A response object that contains information about a specific show.
 */
data class ShowResponse(
    val id: String,
    val name: String,
    val publisher: String,
    @JsonProperty("html_description") val htmlDescription: String,
    val images: List<ImageResponse>
)
