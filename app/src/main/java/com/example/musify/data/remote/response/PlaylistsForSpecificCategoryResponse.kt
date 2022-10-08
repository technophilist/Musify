package com.example.musify.data.remote.response

import com.example.musify.data.remote.response.PlaylistMetadataResponse.OwnerNameWrapper
import com.example.musify.domain.SearchResult
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls

/**
 * A response object that contains a list of [playlists] that are associated
 * with a particular spotify category.
 */
data class PlaylistsForSpecificCategoryResponse(val playlists: PlaylistsForSpecificCategoryResponseItems) {
    /**
     * A response class that contains a list of [PlaylistMetadataWithImageUrlListResponse].
     */
    data class PlaylistsForSpecificCategoryResponseItems(@JsonSetter(contentNulls = Nulls.SKIP) val items: List<PlaylistMetadataWithImageUrlListResponse>)

    /**
     * A response class that is very similar to [PlaylistMetadataResponse]. This class
     * only differs in the fact that it has [imageUrlList] instead of [PlaylistMetadataResponse.images]
     * since the dimension information will not be provided for [PlaylistsForSpecificCategoryResponse].
     */

    data class PlaylistMetadataWithImageUrlListResponse(
        val id: String,
        val name: String,
        @JsonProperty("images") val imageUrlList: List<ImageUrlResponse>,
        @JsonProperty("owner") val ownerName: OwnerNameWrapper,
        @JsonProperty("tracks") val totalNumberOfTracks: PlaylistMetadataResponse.TotalNumberOfTracksWrapper
    )

    /**
     * A response class that contains a [url] for an image.
     */
    data class ImageUrlResponse(val url: String)
}

/**
 * A mapper method used to map an instance of [PlaylistsForSpecificCategoryResponse] to
 * a list of [SearchResult.PlaylistSearchResult].
 */
fun PlaylistsForSpecificCategoryResponse.toPlaylistSearchResultList(): List<SearchResult.PlaylistSearchResult> =
    this.playlists.items.map {
        SearchResult.PlaylistSearchResult(
            id = it.id,
            name = it.name,
            ownerName = it.ownerName.value,
            totalNumberOfTracks = it.totalNumberOfTracks.value.toString(),
            imageUrlString = it.imageUrlList.firstOrNull()?.url
        )
    }
