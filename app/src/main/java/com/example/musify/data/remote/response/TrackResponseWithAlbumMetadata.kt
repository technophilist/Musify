package com.example.musify.data.remote.response

import com.example.musify.domain.SearchResult.TrackSearchResult
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A response object that contains information about a specific track
 * together with the metadata of the associated album.
 */
data class TrackResponseWithAlbumMetadata(
    val id: String,
    val name: String,
    @JsonProperty("preview_url") val previewUrl: String?,
    @JsonProperty("is_playable") val isPlayable: Boolean,
    val explicit: Boolean,
    @JsonProperty("duration_ms") val durationInMillis: Int,
    @JsonProperty("album") val albumMetadata: AlbumMetadataResponse
)

/**
 * A mapper function used to map an instance of [TrackResponseWithAlbumMetadata] to
 * an instance of [TrackSearchResult]. The [imageSize]
 * parameter describes the size of image to be used for the
 * [TrackSearchResult] instance.
 * // todo update docs
 */
fun TrackResponseWithAlbumMetadata.toTrackSearchResult() =
    TrackSearchResult(
        id = id,
        name = name,
        largeImageUrlString = albumMetadata.images[0].url,
        smallImageUrlString = albumMetadata.images[1].url,
        artistsString = albumMetadata.artists.joinToString(",") { it.name },
        trackUrlString = previewUrl
    )
