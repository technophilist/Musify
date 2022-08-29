package com.example.musify.data.remote.response

import com.example.musify.data.utils.MapperImageSize
import com.example.musify.data.utils.getImageResponseForImageSize
import com.example.musify.domain.SearchResult
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A response class that contains a list of tracks in a particular
 * playlist.
 */
data class PlaylistItemsResponse(
    val items: List<TrackResponseWithAlbumMetadataWrapper> // TODO remove PlaylistResponse.TrackResponseWithAlbumMetadataWrapper
) {
    data class TrackResponseWithAlbumMetadataWrapper(@JsonProperty("track") val track: TrackResponseWithAlbumMetadata)
}

fun PlaylistResponse.TrackResponseWithAlbumMetadataWrapper.toTrackSearchResult(imageSize: MapperImageSize) =
    SearchResult.TrackSearchResult(
        id = track.id,
        name = track.name,
        imageUrlString = track.albumMetadata.images.getImageResponseForImageSize(imageSize).url,
        artistsString = track.albumMetadata.artists.joinToString(",") { it.name },
        trackUrlString = track.previewUrl
    )

