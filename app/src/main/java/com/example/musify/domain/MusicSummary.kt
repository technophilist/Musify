package com.example.musify.domain

import java.net.URL

/**
 * A sealed class hierarchy that contains classes that hold a
 * summary of a track, album, song or an Artist.
 *
 * @param id a unique identifying id associated with a particular
 * instance.
 * @param name the name of the summary
 * @param associatedImageUrl the [URL] of the associated image.
 * @param associatedMetadata an optional param that is used to
 * represent a single, important, metadata associated with a
 * particular instance.
 */
sealed class MusicSummary(
    open val id: String,
    open val name: String,
    val associatedImageUrl: URL,
    val associatedMetadata: String? = null,
) {

    /**
     * A data class that contains a summary of a single track.
     */
    data class TrackSummary(
        override val id: String,
        override val name: String,
        val nameOfArtist: String,
        val trackArtUrl: URL,
        val trackUrl: URL
    ) : MusicSummary(id, name, trackArtUrl, nameOfArtist)

    /**
     * A data class that contains a summary of a single album.
     */
    data class AlbumSummary(
        override val id: String,
        override val name: String,
        val nameOfArtist: String,
        val albumArtUrl: URL
    ) : MusicSummary(id, name, albumArtUrl, nameOfArtist)

    /**
     * A data class that contains a summary of a single artist.
     */
    data class ArtistSummary(
        override val id: String,
        override val name: String,
        val profilePictureUrl: URL
    ) : MusicSummary(id, name, profilePictureUrl)

    /**
     * A data class that contains a summary of a single playlist.
     */
    data class PlaylistSummary(
        override val id: String,
        override val name: String,
        val playListArtUrl: URL
    ) : MusicSummary(id, name, playListArtUrl)
}



