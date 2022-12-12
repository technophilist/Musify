package com.example.musify.domain

/**
 * A domain class that represents a specific podcast episode.
 */
data class PodcastEpisode(
    val id: String,
    val title: String,
    val description: String,
    val previewUrl: String?,
    val releaseDateInfo: ReleaseDateInfo,
    val durationInfo: DurationInfo,
    val podcastInfo: PodcastInfo
) {
    /**
     * A domain class that contains the associated podcast information of a
     * [PodcastEpisode].
     */
    data class PodcastInfo(
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
