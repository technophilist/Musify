package com.example.musify.data.remote.response

import com.example.musify.data.utils.MapperImageSize
import com.example.musify.data.utils.getImageResponseForImageSize
import com.example.musify.domain.SearchResult

/**
 * A response object that contains metadata about a specific show.
 */
data class ShowMetadataResponse(
    val id: String,
    val name: String,
    val publisher: String,
    val images: List<ImageResponse>
)

/**
 * A mapper function used to map an instance of [ShowMetadataResponse] to
 * an instance of [SearchResult.PodcastSearchResult].
 */
fun ShowMetadataResponse.toPodcastSearchResult() =
    SearchResult.PodcastSearchResult(
        id = id,
        name = name,
        nameOfPublisher = publisher,
        imageUrlString = images.getImageResponseForImageSize(MapperImageSize.LARGE).url
    )
