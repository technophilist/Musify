package com.example.musify.data.dto

import com.google.gson.annotations.SerializedName

/**
 * A DTO object that contains only the metadata associated with a
 * particular playlist. [PlaylistDTO] contains additional
 * tracks and followers properties.
 */
data class PlaylistMetadataDTO(
    val id: String,
    val name: String,
    val images: List<ImageDTO>,
    @SerializedName("owner") val ownerName: PlaylistDTO.OwnerNameWrapper
)
