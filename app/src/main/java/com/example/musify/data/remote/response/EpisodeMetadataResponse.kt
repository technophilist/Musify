package com.example.musify.data.remote.response

import com.example.musify.data.utils.MapperImageSize
import com.example.musify.data.utils.getFormattedEpisodeReleaseDateAndDuration
import com.example.musify.data.utils.getImageResponseForImageSize
import com.example.musify.domain.SearchResult
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A response object that contains metadata of a specific episode.
 */
data class EpisodeMetadataResponse(
    val id: String,
    @JsonProperty("name") val title: String,
    val description: String,
    @JsonProperty("duration_ms") val durationMillis: Long,
    val images: List<ImageResponse>,
    @JsonProperty("release_date") val releaseDate: String
)

/**
 * A mapper function used to map an instance of [EpisodeMetadataResponse] to
 * an instance of [SearchResult.EpisodeSearchResult].
 * Note: The [SearchResult.EpisodeSearchResult.EpisodeDurationInfo.minutes]
 * is guaranteed to have a minimum value of 1. This means that any episode
 * with a duration lower than 1 minute will be coerced to have a value of
 * 1 minute.
 */
fun EpisodeMetadataResponse.toEpisodeSearchResult(): SearchResult.EpisodeSearchResult {
    val contentInfo = SearchResult.EpisodeSearchResult.EpisodeContentInfo(
        title = this.title,
        description = this.description,
        imageUrlString = images.getImageResponseForImageSize(MapperImageSize.LARGE).url
    )
    val formattedEpisodeReleaseDateAndDuration = getFormattedEpisodeReleaseDateAndDuration(
        releaseDateString = this.releaseDate,
        durationMillis = this.durationMillis
    )
    val episodeDateInfo = SearchResult.EpisodeSearchResult.EpisodeReleaseDateInfo(
        month = formattedEpisodeReleaseDateAndDuration.month,
        day = formattedEpisodeReleaseDateAndDuration.day,
        year = formattedEpisodeReleaseDateAndDuration.year,
    )
    val episodeDurationInfo = SearchResult.EpisodeSearchResult.EpisodeDurationInfo(
        hours = formattedEpisodeReleaseDateAndDuration.hours,
        minutes = formattedEpisodeReleaseDateAndDuration.minutes
    )

    return SearchResult.EpisodeSearchResult(
        id = this.id,
        episodeContentInfo = contentInfo,
        episodeReleaseDateInfo = episodeDateInfo,
        episodeDurationInfo = episodeDurationInfo
    )
}
