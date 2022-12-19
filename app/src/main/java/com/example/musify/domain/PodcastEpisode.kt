package com.example.musify.domain

import android.graphics.Bitmap
import android.text.Spanned
import com.example.musify.musicplayer.MusicPlayerV2

/**
 * A domain class that represents a specific podcast episode.
 */
data class PodcastEpisode(
    val id: String,
    val title: String,
    val description: String,
    val htmlDescription: Spanned,
    val previewUrl: String?,
    val releaseDateInfo: ReleaseDateInfo,
    val durationInfo: DurationInfo,
    val podcastInfo: PodcastInfo
) : Streamable {
    override val streamUrl: String? = previewUrl

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

/**
 * A mapper method used to map an instance of
 * [SearchResult.PodcastSearchResult] to an instance of [MusicPlayerV2.Track].
 */
fun PodcastEpisode.toMusicPlayerTrack(imageBitmap: Bitmap): MusicPlayerV2.Track {
    if (previewUrl == null) throw IllegalStateException("The previewUrl cannot be null during conversion")
    return MusicPlayerV2.Track(
        id = id,
        title = title,
        artistsString = this.podcastInfo.name,
        albumArt = imageBitmap,
        albumArtUrlString = this.podcastInfo.imageUrl,
        trackUrlString = this.previewUrl
    )
}