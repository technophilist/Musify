package com.example.musify.domain

/**
 * A sealed class hierarchy that contains different filters that can be
 * applied to make the home screen display different items.
 */
sealed class HomeFeedFilters(val title: String? = null) {
    object Music : HomeFeedFilters("Music")
    object PodcastsAndShows : HomeFeedFilters("Podcasts & Shows")
    object None : HomeFeedFilters()
}

