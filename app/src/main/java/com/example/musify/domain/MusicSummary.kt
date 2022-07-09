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
    val id: String,
    val name: String,
    val associatedImageUrl: URL,
    val associatedMetadata: String? = null,
) {

    /**
     * A data class that contains a summary of a single track.
     */
    class TrackSummary(
        id: String,
        name: String,
        associatedImageUrl: URL,
        val nameOfArtist: String,
        val trackUrl: URL?,
    ) : MusicSummary(id, name, associatedImageUrl, nameOfArtist)

    /**
     * A data class that contains a summary of a single album.
     */
    class AlbumSummary(
        id: String,
        name: String,
        val nameOfArtist: String,
        val albumArtUrl: URL,
        val yearOfReleaseString: String,
    ) : MusicSummary(id, name, albumArtUrl, nameOfArtist)

    /**
     * A data class that contains a summary of a single artist.
     */
    class ArtistSummary(
        id: String,
        name: String,
        associatedImageUrl: URL
    ) : MusicSummary(id, name, associatedImageUrl)

    /**
     * A data class that contains a summary of a single playlist.
     */
    class PlaylistSummary(
        id: String,
        name: String,
        associatedImageUrl: URL
    ) : MusicSummary(id, name, associatedImageUrl)
}



