package com.example.musify.viewmodels.searchviewmodel

/**
 * An enum that contains the different filters that can be applied to
 * the search results.
 */
enum class SearchFilters(val filterLabel: String) {
    ALL("All"),
    ALBUMS("Albums"),
    TRACKS("Tracks"),
    ARTISTS("Artists"),
    PLAYLISTS("Playlists")
}
