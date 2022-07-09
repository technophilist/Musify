package com.example.musify.data.dto

import com.example.musify.data.utils.MapperImageSize
import com.example.musify.data.utils.getImageDtoForImageSize
import com.example.musify.domain.MusicSummary
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL

/**
 * A DTO object that represents an album. It also contains additional
 * meta data about the album and includes information about the
 * artists.
 */
data class AlbumDTO(
    val id: String,
    val name: String,
    @JsonProperty("album_type") val albumType: String, // album,single or compilation
    val artists: List<ArtistDTOWithNullableImagesAndFollowers>,
    val images: List<ImageDTO>,
    @JsonProperty("release_date") val releaseDate: String,
    @JsonProperty("release_date_precision") val releaseDatePrecision: String, // year, month or day
    @JsonProperty("total_tracks") val totalTracks: Int,
    val tracks: TracksWithoutAlbumMetadataList
) {
    /**
     * A data class that contains the list of tracks associated with
     * a particular [AlbumDTO].
     */
    data class TracksWithoutAlbumMetadataList(@JsonProperty("items") val value: List<TrackDTOWithoutAlbumMetadata>)

    /**
     * A DTO object that contains information about a specific track
     * without containing metadata about the album.
     * [TrackDTOWithAlbumMetadata] contains both, information about
     * the track and the metadata about the associated album.
     */
    data class TrackDTOWithoutAlbumMetadata(
        val id: String,
        val name: String,
        @JsonProperty("preview_url") val previewUrl: String?,
        @JsonProperty("is_playable") val isPlayable: Boolean,
        val explicit: Boolean,
        @JsonProperty("duration_ms") val durationInMillis: Int
    )

    /**
     * A DTO object that contains information about an Artist.
     * [ArtistDTO] mandates these two parameters whereas this object
     * makes [images] and [followers] as nullable type.
     */
    data class ArtistDTOWithNullableImagesAndFollowers(
        val id: String,
        val name: String,
        val images: List<ImageDTO>?,
        val followers: ArtistDTO.Followers?
    )
}

/**
 * A mapper function used to map an instance of [AlbumDTO] to
 * an instance of [MusicSummary.AlbumSummary]. The [imageSize]
 * parameter describes the size of image to be used for the
 * [MusicSummary.AlbumSummary] instance.
 */
fun AlbumDTO.toAlbumSummary(imageSize: MapperImageSize) = MusicSummary.AlbumSummary(
    id = id,
    name = name,
    nameOfArtist = artists.first().name, // TODO multiple artists
    albumArtUrl = URL(images.getImageDtoForImageSize(imageSize).url),
    yearOfReleaseString = releaseDate // TODO accommodate for release data precision
)
