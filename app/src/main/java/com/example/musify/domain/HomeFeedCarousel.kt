package com.example.musify.domain

/**
 * A domain class the represents a single card in a home feed
 * carousel with the specified [id],[imageUrlString] and [caption].
 * It also contains the [associatedSearchResult] object for this specific
 * carousel card.
 */
data class HomeFeedCarouselCardInfo(
    val id: String,
    val imageUrlString: String,
    val caption: String,
    val associatedSearchResult: SearchResult
)

/**
 * A domain class that contain the [title] and [associatedCards]
 * of a single home feed carousel.
 * @param id the unique id of the car
 */
data class HomeFeedCarousel(
    val id: String,
    val title: String,
    val associatedCards: List<HomeFeedCarouselCardInfo>
)
