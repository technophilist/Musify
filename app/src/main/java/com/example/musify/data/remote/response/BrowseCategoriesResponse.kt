package com.example.musify.data.remote.response

/**
 * A response object that contains a list of different [categories] that
 * are available to a specific region and local. These categories can
 * then be used to fetch playlists that belong to the same category.
 */
data class BrowseCategoriesResponse(val categories: BrowseCategoryItemsResponse) {
    /**
     * A response object that contains a list of [BrowseCategoriesResponse].
     */
    data class BrowseCategoryItemsResponse(val items: List<BrowseCategoryResponse>)

    /**
     * A response object that contains the [id] and [name] associated with
     * a specific browse category.
     */
    data class BrowseCategoryResponse(
        val id: String,
        val name: String
    )
}
