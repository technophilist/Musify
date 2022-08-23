package com.example.musify.data.remote.response

import com.example.musify.data.utils.MapperImageSize
import com.example.musify.data.utils.getImageResponseForImageSize
import com.example.musify.domain.MusicSummary
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL

/**
 * A response object that represents an album. It also contains additional
 * meta data about the album and includes information about the
 * artists.
 */
data class AlbumResponse(
    val id: String,
    val name: String,
    @JsonProperty("album_type") val albumType: String, // album,single or compilation
    val artists: List<ArtistResponseWithNullableImagesAndFollowers>,
    val images: List<ImageResponse>,
    @JsonProperty("release_date") val releaseDate: String,
    @JsonProperty("release_date_precision") val releaseDatePrecision: String, // year, month or day
    @JsonProperty("total_tracks") val totalTracks: Int,
    val tracks: TracksWithoutAlbumMetadataListResponse
) {
    /**
     * A data class that contains the list of tracks associated with
     * a particular [AlbumResponse].
     */
    data class TracksWithoutAlbumMetadataListResponse(@JsonProperty("items") val value: List<TrackResponseWithoutAlbumMetadataResponse>)

    /**
     * A response object that contains information about a specific track
     * without containing metadata about the album.
     * [TrackResponseWithAlbumMetadata] contains both, information about
     * the track and the metadata about the associated album.
     */
    data class TrackResponseWithoutAlbumMetadataResponse(
        val id: String,
        val name: String,
        @JsonProperty("preview_url") val previewUrl: String?,
        @JsonProperty("is_playable") val isPlayable: Boolean,
        val explicit: Boolean,
        @JsonProperty("duration_ms") val durationInMillis: Int
    )

    /**
     * A response object that contains information about an Artist.
     * [ArtistResponse] mandates these two parameters whereas this object
     * makes [images] and [followers] as nullable type.
     */
    data class ArtistResponseWithNullableImagesAndFollowers(
        val id: String,
        val name: String,
        val images: List<ImageResponse>?,
        val followers: ArtistResponse.Followers?
    )
}

/**
 * A mapper function used to map an instance of [AlbumResponse] to
 * an instance of [MusicSummary.AlbumSummary]. The [imageSize]
 * parameter describes the size of image to be used for the
 * [MusicSummary.AlbumSummary] instance.
 */
fun AlbumResponse.toAlbumSummary(imageSize: MapperImageSize) = MusicSummary.AlbumSummary(
    id = id,
    name = name,
    nameOfArtist = artists.first().name, // TODO multiple artists
    albumArtUrl = URL(images.getImageResponseForImageSize(imageSize).url),
    yearOfReleaseString = releaseDate // TODO accommodate for release data precision
)
