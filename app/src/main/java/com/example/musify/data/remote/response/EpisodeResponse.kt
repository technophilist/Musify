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
    val episodeDurationInfo = SearchResult.EpisodeSearchResult.EpisodeDurationInfo(
        hours = duration.toHoursPart(),
        minutes = duration.toMinutesPart()
    )

    return SearchResult.EpisodeSearchResult(
        id = this.id,
        episodeContentInfo = contentInfo,
        episodeReleaseDateInfo = episodeDateInfo,
        episodeDurationInfo = episodeDurationInfo
    )
}
