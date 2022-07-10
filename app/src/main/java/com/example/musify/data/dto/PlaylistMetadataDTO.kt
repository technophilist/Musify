package com.example.musify.data.dto

import com.example.musify.data.utils.getImageDtoForImageSize
import com.example.musify.domain.MusicSummary
import com.example.musify.domain.searchresult.PlaylistSearchResult
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
 * an instance of [MusicSummary.PlaylistSummary].
 *
 * Note:[getImageDtoForImageSize] cannot be used because playlists usually
 * contain only a single image. Therefore, the url of the first image
 * is mapped to [MusicSummary.PlaylistSummary.associatedImageUrl].
 */
fun PlaylistMetadataDTO.toPlaylistSummary() = MusicSummary.PlaylistSummary(
    id = id,
    name = name,
    associatedImageUrl = URL(images.first().url)
)

/**
 * A mapper function used to map an instance of [PlaylistMetadataDTO] to
 * an instance of [PlaylistSearchResult].
 *
 * Note:[getImageDtoForImageSize] cannot be used because playlists usually
 * contain only a single image. Therefore, the url of the first image
 * is mapped to [PlaylistSearchResult.imageUrlString].
 */
fun PlaylistMetadataDTO.toPlaylistSearchResult() = PlaylistSearchResult(
    id = id,
    name = name,
    imageUrlString = images.first().url
)