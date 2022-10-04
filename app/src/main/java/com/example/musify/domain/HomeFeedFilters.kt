package com.example.musify.domain

/**
 * An enum that contains the different filters that can be applied to
 * the home screen.
 * @param title the title of the filter.
 */
enum class HomeFeedFilters(val title:String) {
    MUSIC("Music"),
    PODCASTS_AND_SHOWS("Podcasts & Shows")
}

