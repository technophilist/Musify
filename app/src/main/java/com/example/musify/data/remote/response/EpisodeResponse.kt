package com.example.musify.data.remote.response

import com.example.musify.data.utils.MapperImageSize
import com.example.musify.data.utils.getFormattedEpisodeReleaseDateAndDuration
import com.example.musify.data.utils.getImageResponseForImageSize
import com.example.musify.domain.PodcastEpisode
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A response object that contains information about a specific episode.
 */
data class EpisodeResponse(
    val id: String,
    @JsonProperty("name") val title: String,
    val description: String,
    @JsonProperty("html_description")
    val htmlDescription: String,
    @JsonProperty("duration_ms") val durationMillis: Long,
    @JsonProperty("release_date") val releaseDate: String,
    @JsonProperty("audio_preview_url") val previewUrl: String?,
    val show: EpisodeShowResponse
) {
    data class EpisodeShowResponse(
        val id: String,
        val name: String,
        val images: List<ImageResponse>
    )
}

/**
 * A mapper function used to map an instance of [EpisodeResponse] to
 * an instance of [PodcastEpisode]. The [imageSize] parameter determines the size of image
 * to be used for the [PodcastEpisode] instance.
 * Note: The [PodcastEpisode.DurationInfo.minutes] is guaranteed to have a minimum value of 1.
 * This means that any episode with a duration lower than 1 minute will be coerced to have
 * a value of 1 minute.
 */
fun EpisodeResponse.toPodcastEpisode(imageSize: MapperImageSize): PodcastEpisode {
    val formattedEpisodeReleaseDateAndDuration = getFormattedEpisodeReleaseDateAndDuration(
        releaseDateString = this.releaseDate,
        durationMillis = this.durationMillis
    )
    val releaseDateInfo = PodcastEpisode.ReleaseDateInfo(
        month = formattedEpisodeReleaseDateAndDuration.month,
        day = formattedEpisodeReleaseDateAndDuration.day,
        year = formattedEpisodeReleaseDateAndDuration.year,
    )
 
    val durationInfo = PodcastEpisode.DurationInfo(
        hours = formattedEpisodeReleaseDateAndDuration.hours,
        minutes = formattedEpisodeReleaseDateAndDuration.minutes
    )

    return PodcastEpisode(
        id = this.id,
        title = this.title,
        description = this.description,
        htmlDescription = this.htmlDescription,
        previewUrl = previewUrl,
        releaseDateInfo = releaseDateInfo,
        durationInfo = durationInfo,
        podcastShowInfo = PodcastEpisode.PodcastShowInfo(
            id = this.show.id,
            name = this.show.name,
            imageUrl = this.show.images.getImageResponseForImageSize(imageSize).url
        )
    )
}