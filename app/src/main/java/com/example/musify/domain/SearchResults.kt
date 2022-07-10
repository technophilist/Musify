package com.example.musify.domain

/**
 * A class that models a search result. It contains all the [tracks],
 * [albums],[artists] and [playlists] that matched a search query.
 */
data class SearchResults(
    val tracks:List<MusicSummary.TrackSummary>,
    val albums:List<MusicSummary.AlbumSummary>,
    val artists:List<MusicSummary.ArtistSummary>,
    val playlists:List<MusicSummary.PlaylistSummary>,
)
