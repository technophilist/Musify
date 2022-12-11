package com.example.musify.data.remote.response

import com.example.musify.data.utils.MapperImageSize
import com.example.musify.data.utils.getImageResponseForImageSize
import com.example.musify.domain.SearchResult
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Duration
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

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
 * an instance of [SearchResult.EpisodeSearchResult]. The [imageSize]
 * parameter determines the size of image to be used for the
 * [SearchResult.EpisodeSearchResult] instance.
 * Note: The [SearchResult.EpisodeSearchResult.EpisodeDurationInfo.minutes]
 * is guaranteed to have a minimum value of 1. This means that any episode
 * with a duration lower than 1 minute will be coerced to have a value of
 * 1 minute.
 */
fun EpisodeMetadataResponse.toEpisodeSearchResult(imageSize: MapperImageSize): SearchResult.EpisodeSearchResult {
    val contentInfo = SearchResult.EpisodeSearchResult.EpisodeContentInfo(
        title = this.title,
        description = this.description,
        imageUrlString = images.getImageResponseForImageSize(imageSize).url
    )
    val localDate = LocalDate.parse(this.releaseDate)
    val episodeDateInfo = SearchResult.EpisodeSearchResult.EpisodeReleaseDateInfo(
        month = localDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
        day = localDate.dayOfMonth,
        year = localDate.year,
    )

    val duration = Duration.ofMillis(this.durationMillis)
    // Equivalent to duration#toHoursPart. Not available in java 8 desugared library
    val hours = (duration.toHours() % 24).toInt()
    // Equivalent to duration#toMinutesPart. Not available in java 8 desugared library
    val minutes = (duration.toMinutes() % 60).toInt().coerceAtLeast(1)
    val episodeDurationInfo = SearchResult.EpisodeSearchResult.EpisodeDurationInfo(
        hours = hours,
        minutes = minutes
    )

    return SearchResult.EpisodeSearchResult(
        id = this.id,
        episodeContentInfo = contentInfo,
        episodeReleaseDateInfo = episodeDateInfo,
        episodeDurationInfo = episodeDurationInfo
    )
}
