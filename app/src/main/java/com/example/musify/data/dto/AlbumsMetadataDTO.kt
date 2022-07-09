package com.example.musify.data.dto

import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.MusicSummary
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A DTO that contains a list of albums together with additional
 * metadata.
 */
data class AlbumsMetadataDTO(
    val items: List<AlbumMetadataDTO>,
    val limit: Int, // indicates the number of items in the list
    @JsonProperty("next") val nextPageUrlString: String,
    val offset: Int,
    @JsonProperty("previous") val previousPageUrlString: String?,
    @JsonProperty("total") val totalNumberOfItemsAvailable: Int
)

/**
 * A mapper function used to map an instance of [AlbumsMetadataDTO] to
 * a list of [MusicSummary.AlbumSummary]. The [imageSize]
 * parameter describes the size of image to be used for each
 * [MusicSummary.AlbumSummary] instance.
 */
fun AlbumsMetadataDTO.toAlbumSummaryList(imageSize: MapperImageSize) = items.map {
    it.toAlbumSummary(imageSize)
}
