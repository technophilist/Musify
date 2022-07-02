package com.example.musify.data.dto

import com.google.gson.annotations.SerializedName

/**
 * A DTO that contains a list of albums together with additional
 * metadata.
 */
data class AlbumsMetadataDTO(
    val items: List<AlbumMetadataDTO>,
    val limit: Int, // indicates the number of items in the list
    @SerializedName("next") val nextPageUrlString: String,
    val offset: Int,
    @SerializedName("previous") val previousPageUrlString: Any,
    @SerializedName("total") val totalNumberOfItemsAvailable: Int
)