package com.example.musify.domain

import com.example.musify.domain.searchresult.AlbumSearchResult
import com.example.musify.domain.searchresult.ArtistSearchResult
import com.example.musify.domain.searchresult.PlaylistSearchResult
import com.example.musify.domain.searchresult.TrackSearchResult

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
