package com.example.musify.data.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A DTO object that contains only the metadata associated with a
 * particular playlist. [PlaylistDTO] contains additional
 * tracks and followers properties.
 */
data class PlaylistMetadataDTO(
    val id: String,
    val name: String,
    val images: List<ImageDTO>,
    @JsonProperty("owner") val ownerName: PlaylistDTO.OwnerNameWrapper
)
