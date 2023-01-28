package com.example.musify.data.remote.response

import com.example.musify.domain.SearchResults
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A response that contains the results of a search operation.
 * All the properties are nullable because a search operation
 * for just [tracks],[albums],[artists],[playlists],[shows] or
 * a combination of any of the above can be made, in which
 * case, the other properties will be null.
 */
data class SearchResultsResponse(
    val tracks: Tracks?,
    val albums: Albums?,
    val artists: Artists?,
    val playlists: Playlists?,
    val shows: Shows?,
    val episodes: Episodes?
) {
    data class Tracks(@JsonProperty("items") val value: List<TrackResponseWithAlbumMetadata>)
    data class Albums(@JsonProperty("items") val value: List<AlbumMetadataResponse>)
    data class Artists(@JsonProperty("items") val value: List<ArtistResponse>)
    data class Playlists(@JsonProperty("items") val value: List<PlaylistMetadataResponse>)
    data class Shows(@JsonProperty("items") val value: List<ShowMetadataResponse>)
    data class Episodes(@JsonProperty("items") val value: List<EpisodeMetadataResponse>)
}

/**
 * A mapper function used to map an instance of [SearchResultsResponse] to
 * an instance of [SearchResults]. The [imageSize] parameter describes
 * the size of image to be used for the for associated [SearchResults]
 * instances excluding [SearchResults.playlists].
 */
fun SearchResultsResponse.toSearchResults() = SearchResults(
    tracks = tracks?.value?.map { it.toTrackSearchResult() } ?: emptyList(),
    albums = albums?.value?.map { it.toAlbumSearchResult() } ?: emptyList(),
    artists = artists?.value?.map { it.toArtistSearchResult() } ?: emptyList(),
    playlists = playlists?.value?.map { it.toPlaylistSearchResult() } ?: emptyList(),
    shows = shows?.value?.map { it.toPodcastSearchResult() } ?: emptyList(),
    episodes = episodes?.value?.map { it.toEpisodeSearchResult() } ?: emptyList()
)

