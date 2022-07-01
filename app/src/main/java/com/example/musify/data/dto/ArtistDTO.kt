package com.example.musify.data.dto


/**
 * A DTO object that contains information about an Artist.
 */
data class ArtistDTO(
    val id: String,
    val name: String,
    val images: List<ImageDTO>,
    val followers: Followers
) {
    /**
     * A DTO class that holds the number of followers that follow
     * an Artist.
     */
    data class Followers(val total: String)
}

