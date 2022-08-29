package com.example.musify.data.remote.response

import com.example.musify.data.utils.getImageResponseForImageSize
import com.example.musify.domain.SearchResult.PlaylistSearchResult
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A response object that contains only the metadata associated with a
 * particular playlist. [PlaylistResponse] contains additional
 * tracks and followers properties.
 */
data class PlaylistMetadataResponse(
    val id: String,
    val name: String,
    val images: List<ImageResponse>,
    @JsonProperty("owner") val ownerName: OwnerNameWrapper,
    @JsonProperty("tracks") val totalNumberOfTracks: TotalNumberOfTracksWrapper
) {
    data class TotalNumberOfTracksWrapper(@JsonProperty("total") val value: Int)
    data class OwnerNameWrapper(@JsonProperty("display_name") val value: String)
}

/**
 * A mapper function used to map an instance of [PlaylistMetadataResponse] to
 * an instance of [PlaylistSearchResult].
 *
 * Note:[getImageResponseForImageSize] cannot be used because playlists usually
 * contain only a single image. Therefore, the url of the first image
 * is mapped to [PlaylistSearchResult.imageUrlString].
 */
fun PlaylistMetadataResponse.toPlaylistSearchResult() = PlaylistSearchResult(
    id = id,
    name = name,
    ownerName = ownerName.value,
    imageUrlString = images.firstOrNull()?.url,
    totalNumberOfTracks = totalNumberOfTracks.value.toString()
)