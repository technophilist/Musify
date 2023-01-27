package com.example.musify.domain

import android.content.Context
import com.example.musify.utils.generateMusifyDateAndDurationString

/**
 * A domain class that represents a specific podcast episode.
 */
data class PodcastEpisode(
    val id: String,
    val title: String,
    val episodeImageUrl: String,
    val description: String,
    val htmlDescription: String,
    val previewUrl: String?,
    val releaseDateInfo: ReleaseDateInfo,
    val durationInfo: DurationInfo,
    val podcastShowInfo: PodcastShowInfo
) : Streamable {
    override val streamInfo = StreamInfo(
        streamUrl = previewUrl,
        imageUrl = podcastShowInfo.imageUrl,
        title = title,
        subtitle = podcastShowInfo.name
    )

    /**
     * A domain class that contains the associated podcast show information of a
     * [PodcastEpisode].
     */
    data class PodcastShowInfo(
        val id: String,
        val name: String,
        val imageUrl: String
    )

    /**
     * A domain class that contains the date information of a [PodcastEpisode].
     */
    data class ReleaseDateInfo(val month: String, val day: Int, val year: Int)

    /**
     * A domain class that contains the duration information of a [PodcastEpisode].
     */
    data class DurationInfo(val hours: Int, val minutes: Int)
}

/**
 * A utility method used to get a string that contains date and duration
 * information in a formatted manner.
 * @see generateMusifyDateAndDurationString
 */
fun PodcastEpisode.getFormattedDateAndDurationString(context: Context): String =
    generateMusifyDateAndDurationString(
        context = context,
        month = releaseDateInfo.month,
        day = releaseDateInfo.day,
        year = releaseDateInfo.year,
        hours = durationInfo.hours,
        minutes = durationInfo.minutes
    )

/**
 * A method used to check whether two instances of [PodcastEpisode]'s are equal,
 * ignoring the [PodcastEpisode.PodcastShowInfo.imageUrl].
 * Since the objects might have urls for different image sizes based on the
 * size mentioned by the caller, both objects may represent the same podcast
 * episode, but they might not be equal because the urls are different. Hence,
 * if they are not equal, this method will try creating copies of the object with
 * the the same imageUrl for both, and the tries checking them for equality.
 *
 * This might be useful especially in cases where a global state of the currently
 * playing [PodcastEpisode] is needed to be maintained and highlighted in different
 * screens if that episode is present. For example, lets say that there are two screens,
 * wherein one screen might display the [PodcastEpisode] and another screen may list all
 * the episodes. Let's also assume that the episode can be played from either of these
 * screens. If the podcast episode where to be played from either of the screens,
 * it must also be highlighted in both the screens to indicate that the same track
 * is playing. But, different screens might request the [PodcastEpisode] to
 * be played with differing image sizes. This makes direct comparison of these
 * objects to be evaluated to false. Which in-turn, makes highlighting the currently
 * playing episode in different screens, cumbersome. In these types of scenarios,
 * this method comes in very handy.
 */
fun PodcastEpisode?.equalsIgnoringImageSize(other: PodcastEpisode?): Boolean {
    if (other == null || this == null) return false
    if (this == other) return true
    val podcastInfoOfThisWithEmptyImageUrl = this.podcastShowInfo.copy(imageUrl = "")
    val podcastInfoOfOtherWithEmptyImageUrl = other.podcastShowInfo.copy(imageUrl = "")
    val thisWithEmptyImageUrl = this.copy(podcastShowInfo = podcastInfoOfThisWithEmptyImageUrl)
    val otherWithEmptyImageUrl = other.copy(podcastShowInfo = podcastInfoOfOtherWithEmptyImageUrl)
    return thisWithEmptyImageUrl == otherWithEmptyImageUrl
}