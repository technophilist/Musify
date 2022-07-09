package com.example.musify.data.dto

import com.example.musify.data.utils.MapperImageSize
import com.example.musify.data.utils.getImageDtoForImageSize
import com.example.musify.domain.MusicSummary
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL

/**
 * A DTO object that contains information about a specific track
 * together with the metadata of the associated album.
 */
data class TrackDTOWithAlbumMetadata(
    val id: String,
    val name: String,
    @JsonProperty("preview_url") val previewUrl: String?,
    @JsonProperty("is_playable") val isPlayable: Boolean,
    val explicit: Boolean,
    @JsonProperty("duration_ms") val durationInMillis: Int,
    @JsonProperty("album") val albumMetadata: AlbumMetadataDTO
)

/**
 * A mapper function used to map an instance of [TrackDTOWithAlbumMetadata] to
 * an instance of [MusicSummary.TrackSummary]. The [imageSize]
 * parameter describes the size of image to be used for the
 * [MusicSummary.TrackSummary] instance.
 */
fun TrackDTOWithAlbumMetadata.toTrackSummary(imageSize: MapperImageSize) =
    MusicSummary.TrackSummary(
        id = id,
        name = name,
        associatedImageUrl = URL(albumMetadata.images.getImageDtoForImageSize(imageSize).url),
        albumName = albumMetadata.name,
        trackUrl = previewUrl?.let(::URL)
    )

