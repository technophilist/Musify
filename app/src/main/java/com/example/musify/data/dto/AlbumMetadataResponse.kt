package com.example.musify.data.dto

import com.example.musify.data.utils.MapperImageSize
import com.example.musify.data.utils.getImageDtoForImageSize
import com.example.musify.domain.MusicSummary
import com.example.musify.domain.SearchResult.AlbumSearchResult
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL

/**
 * A DTO object that contains metadata about a specific album.
 * Note: The object only contains metadata. It doesn't contain
 * the track list. [AlbumResponse] contains the track list in addition
 * to the metadata.
 */
data class AlbumMetadataResponse(
    val id: String,
    val name: String,
    @JsonProperty("album_type") val albumType: String, // album,single or compilation
    val artists: List<ArtistInfoResponse>,
    val images: List<ImageResponse>,
    @JsonProperty("release_date") val releaseDate: String,
    @JsonProperty("release_date_precision") val releaseDatePrecision: String, // year, month or day
    @JsonProperty("total_tracks") val totalTracks: Int,
    val type: String
) {
    /**
     * A DTO object associated with [AlbumMetadataResponse] that contains information
     * about an artist.
     */
    data class ArtistInfoResponse(
        val id: String,
        val name: String
    )
}

/**
 * A mapper function used to map an instance of [AlbumMetadataResponse] to
 * an instance of [MusicSummary.AlbumSummary]. The [imageSize]
 * parameter describes the size of image to be used for the
 * [MusicSummary.AlbumSummary] instance.
 */
fun AlbumMetadataResponse.toAlbumSummary(imageSize: MapperImageSize) = MusicSummary.AlbumSummary(
    id = id,
    name = name,
    nameOfArtist = artists.first().name,  // TODO
    albumArtUrl = URL(images.getImageDtoForImageSize(imageSize).url),
    yearOfReleaseString = releaseDate // TODO
)

/**
 * A mapper function used to map an instance of [AlbumMetadataResponse] to
 * an instance of [AlbumSearchResult]. The [imageSize]
 * parameter describes the size of image to be used for the
 * [AlbumSearchResult] instance.
 */
fun AlbumMetadataResponse.toAlbumSearchResult(imageSize: MapperImageSize) = AlbumSearchResult(
    id = id,
    name = name,
    artistsString = artists.joinToString(",") { it.name },
    albumArtUrlString = images.getImageDtoForImageSize(imageSize).url,
    yearOfReleaseString = releaseDate // TODO
)