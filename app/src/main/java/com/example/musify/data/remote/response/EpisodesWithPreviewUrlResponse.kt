package com.example.musify.data.remote.response

import com.example.musify.data.remote.response.EpisodesWithPreviewUrlResponse.EpisodeMetadataResponseWithPreviewUrl
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.data.utils.getFormattedEpisodeReleaseDateAndDuration
import com.example.musify.data.utils.getImageResponseForImageSize
import com.example.musify.domain.PodcastEpisode
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
        @JsonProperty("html_description") val htmlDescription: String,
        @JsonProperty("duration_ms") val durationMillis: Long,
        val images: List<ImageResponse>,
        @JsonProperty("release_date") val releaseDate: String,
        @JsonProperty("audio_preview_url") val previewUrl: String?
    )
}

/**
 * A mapper function used to map an instance of [EpisodeMetadataResponseWithPreviewUrl]
 * to an instance of [PodcastEpisode].
 * TODO : each episode has a unique image. But [PodcastEpisode] doeesn't accomodate for
 * that.
 * */
fun EpisodeMetadataResponseWithPreviewUrl.toPodcastEpisode(
    imageSizeForPodcastShowImage: MapperImageSize,
    imageSizeForEpisodeImage:MapperImageSize = imageSizeForPodcastShowImage,
    showResponse: ShowResponse
): PodcastEpisode {
    val formattedDateAndDuration = getFormattedEpisodeReleaseDateAndDuration(
        releaseDateString = releaseDate,
        durationMillis = durationMillis
    )
    val releaseDateInfo = PodcastEpisode.ReleaseDateInfo(
        month = formattedDateAndDuration.month,
        day = formattedDateAndDuration.day,
        year = formattedDateAndDuration.year
    )
    val durationInfo = PodcastEpisode.DurationInfo(
        hours = formattedDateAndDuration.hours,
        minutes = formattedDateAndDuration.minutes
    )
    val podcastInfo = PodcastEpisode.PodcastShowInfo(
        id = showResponse.id,
        name = showResponse.name,
        imageUrl = showResponse.images.getImageResponseForImageSize(MapperImageSize.LARGE).url
    )
    return PodcastEpisode(
        id = id,
        title = title,
        description = description,
        episodeImageUrl = images.getImageResponseForImageSize(MapperImageSize.LARGE).url,
        htmlDescription = htmlDescription,
        previewUrl = previewUrl,
        releaseDateInfo = releaseDateInfo,
        durationInfo = durationInfo,
        podcastShowInfo = podcastInfo
    )
}