package com.example.musify.data.dto

import com.example.musify.data.utils.MapperImageSize
import com.example.musify.data.utils.getImageDtoForImageSize
import com.example.musify.domain.MusicSummary
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL

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

/**
 * A mapper function used to map an instance of [PlaylistMetadataDTO] to
 * an instance of [MusicSummary.PlaylistSummary]. The [imageSize]
 * parameter describes the size of image to be used for the
 * [MusicSummary.PlaylistSummary] instance.
 */
fun PlaylistMetadataDTO.toPlaylistSummary(
    imageSize: MapperImageSize
) = MusicSummary.PlaylistSummary(
    id = id,
    name = name,
    associatedImageUrl = URL(images.getImageDtoForImageSize(imageSize).url)
)
