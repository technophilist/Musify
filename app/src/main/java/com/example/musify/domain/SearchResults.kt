package com.example.musify.domain

import com.example.musify.domain.SearchResult.AlbumSearchResult
import com.example.musify.domain.SearchResult.ArtistSearchResult
import com.example.musify.domain.SearchResult.PlaylistSearchResult
import com.example.musify.domain.SearchResult.TrackSearchResult

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
