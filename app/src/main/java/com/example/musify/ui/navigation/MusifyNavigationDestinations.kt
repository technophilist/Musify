package com.example.musify.ui.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class MusifyNavigationDestinations(val route: String) {
    object SearchScreen :
        MusifyNavigationDestinations("MusifyNavigationDestinations.SearchScreen")

    object ArtistDetailScreen :
        MusifyNavigationDestinations("MusifyNavigationDestinations.ArtistDetailScreen/{artistId}/{artistName}/{encodedImageUrlString}") {
        const val NAV_ARG_ARTIST_ID = "artistId"
        const val NAV_ARG_ARTIST_NAME = "artistName"
        const val NAV_ARG_ENCODED_IMAGE_URL_STRING = "encodedImageUrlString"
        fun buildRoute(
            artistId: String,
            artistName: String,
            imageUrlString: String?
        ): String {
            val encodedImageUrl =
                URLEncoder.encode(imageUrlString ?: "", StandardCharsets.UTF_8.toString())
            return "MusifyNavigationDestinations.ArtistDetailScreen/$artistId/$artistName/$encodedImageUrl"
        }

    }
}