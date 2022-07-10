package com.example.musify.domain

import com.example.musify.domain.SearchResult.*

/**
 * A class that models a search result. It contains all the [tracks],
 * [albums],[artists] and [playlists] that matched a search query.
 */
data class SearchResults(
    val tracks: List<TrackSearchResult>,
    val albums: List<AlbumSearchResult>,
    val artists: List<ArtistSearchResult>,
    val playlists: List<PlaylistSearchResult>,
)

/**
 * A utility function that returns an instance of  [SearchResults] with
 * empty read-only lists.
 */
fun emptySearchResults() = SearchResults(
    tracks = emptyList(),
    albums = emptyList(),
    artists = emptyList(),
    playlists = emptyList()
)
