package com.example.musify.domain

/**
 * A domain class that contains a list of [associatedPlaylists] fir a specific
 * playlist category. Eg. "Focus" could be a category that contains playlists
 * that contain ambient music.
 * @param categoryId the id of the category
 * @param nameOfCategory the name of the category. Examples - ["Top lists","Summer","Sleep"]
 */
data class PlaylistsForCategory(
    val categoryId: String,
    val nameOfCategory: String,
    val associatedPlaylists: List<SearchResult.PlaylistSearchResult>
)

/**
 * A mapper method used to map an instance of [PlaylistsForCategory] to
 * an instance of [HomeFeedCarousel].
 */
fun PlaylistsForCategory.toHomeFeedCarousel(): HomeFeedCarousel = associatedPlaylists.map {
    HomeFeedCarouselCardInfo(
        id = it.id,
        imageUrlString = it.imageUrlString ?: "",
        caption = it.name,
        associatedSearchResult = it
    )
}.let {
    HomeFeedCarousel(
        id = categoryId,
        title = nameOfCategory,
        associatedCards = it
    )
}

