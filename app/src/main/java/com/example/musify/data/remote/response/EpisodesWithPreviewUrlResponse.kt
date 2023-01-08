package com.example.musify.data.remote.response

import com.example.musify.data.remote.response.EpisodesWithPreviewUrlResponse.EpisodeMetadataResponseWithPreviewUrl
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.data.utils.getFormattedEpisodeReleaseDateAndDuration
import com.example.musify.data.utils.getImageResponseForImageSize
import com.example.musify.domain.SearchResult
import com.fasterxml.jackson.annotation.JsonProperty


/**
 * A response object that contains a list of [EpisodeMetadataResponseWithPreviewUrl].
 */
data class EpisodesWithPreviewUrlResponse(val items: List<EpisodeMetadataResponseWithPreviewUrl>) {
    /**
     * A response object that contains metadata of a specific episode together
     * with a [previewUrl]. It is essentially the same as [EpisodeMetadataResponse]
     * with a field for [previewUrl].
     */
    data class EpisodeMetadataResponseWithPreviewUrl(
        val id: String,
        @JsonProperty("name") val title: String,
        val description: String,
        @JsonProperty("duration_ms") val durationMillis: Long,
        val images: List<ImageResponse>,
        @JsonProperty("release_date") val releaseDate: String,
        @JsonProperty("preview_url") val previewUrl: String?
    )
}

/**
 * A mapper function used to map an instance of [EpisodeMetadataResponseWithPreviewUrl]
 * to an instance of [SearchResult.StreamableEpisodeSearchResult]. The [imageSize]
 * parameter will be used to determine the size of the image contained in the
 * [SearchResult.EpisodeSearchResult.EpisodeContentInfo.imageUrlString] object of the
 * returned [SearchResult.StreamableEpisodeSearchResult] instance.
 */
fun EpisodeMetadataResponseWithPreviewUrl.toStreamableEpisodeSearchResult(
    imageSize: MapperImageSize
): SearchResult.StreamableEpisodeSearchResult {
    val formattedDateAndDuration = getFormattedEpisodeReleaseDateAndDuration(
        releaseDateString = releaseDate,
        durationMillis = durationMillis
    )
    val episodeContentInfo = SearchResult.EpisodeSearchResult.EpisodeContentInfo(
        title = title,
        description = description,
        imageUrlString = images.getImageResponseForImageSize(imageSize).url
    )
    val episodeReleaseDateInfo = SearchResult.EpisodeSearchResult.EpisodeReleaseDateInfo(
        month = formattedDateAndDuration.month,
        day = formattedDateAndDuration.day,
        year = formattedDateAndDuration.year
    )
    val episodeDurationInfo = SearchResult.EpisodeSearchResult.EpisodeDurationInfo(
        hours = formattedDateAndDuration.hours,
        minutes = formattedDateAndDuration.minutes
    )
    return SearchResult.StreamableEpisodeSearchResult(
        id = id,
        episodeUrlString = previewUrl,
        episodeContentInfo = episodeContentInfo,
        episodeReleaseDateInfo = episodeReleaseDateInfo,
        episodeDurationInfo = episodeDurationInfo
    )
}