package com.example.musify.ui.navigation

import com.example.musify.domain.SearchResult
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class MusifyNavigationDestinations(val route: String) {
    object SearchScreen :
        MusifyNavigationDestinations("MusifyNavigationDestinations.SearchScreen")

    object ArtistDetailScreen :
        MusifyNavigationDestinations("MusifyNavigationDestinations.ArtistDetailScreen/{artistId}/{artistName}?encodedUrlString={encodedImageUrlString}") {
        const val NAV_ARG_ARTIST_ID = "artistId"
        const val NAV_ARG_ARTIST_NAME = "artistName"
        const val NAV_ARG_ENCODED_IMAGE_URL_STRING = "encodedImageUrlString"

        fun buildRoute(artistSearchResult: SearchResult.ArtistSearchResult): String {
            val routeWithoutUrl =
                "MusifyNavigationDestinations.ArtistDetailScreen/${artistSearchResult.id}/${artistSearchResult.name}"
            if (artistSearchResult.imageUrlString == null) return routeWithoutUrl
            val encodedImageUrl = URLEncoder.encode(
                artistSearchResult.imageUrlString,
                StandardCharsets.UTF_8.toString()
            )
            return "$routeWithoutUrl?encodedUrlString=$encodedImageUrl"
        }

    }

    object AlbumDetailScreen :
        MusifyNavigationDestinations("MusifyNavigationDestinations.AlbumDetailScreen/{albumId}/{albumName}/{artistsString}/{yearOfReleaseString}/{encodedImageUrlString}") {
        const val NAV_ARG_ALBUM_ID = "albumId"
        const val NAV_ARG_ALBUM_NAME = "albumName"
        const val NAV_ARG_ARTISTS_STRING = "artistsString"
        const val NAV_ARG_ENCODED_IMAGE_URL_STRING = "encodedImageUrlString"
        const val NAV_ARG_YEAR_OF_RELEASE_STRING = "yearOfReleaseString"

        fun buildRoute(albumSearchResult: SearchResult.AlbumSearchResult): String {
            val encodedImageUrlString = URLEncoder.encode(
                albumSearchResult.albumArtUrlString,
                StandardCharsets.UTF_8.toString()
            )
            return "MusifyNavigationDestinations.AlbumDetailScreen" +
                    "/${albumSearchResult.id}" +
                    "/${albumSearchResult.name}" +
                    "/${albumSearchResult.artistsString}" +
                    "/${albumSearchResult.yearOfReleaseString.substringBefore("-")}" +
                    "/${encodedImageUrlString}"
        }
    }

    object PlaylistDetailScreen :
        MusifyNavigationDestinations("MusifyNavigationDestinations.AlbumDetailScreen/{playlistId}/{playlistName}?encodedImageUrlString={encodedImageUrlString}") {
        const val NAV_ARG_PLAYLIST_ID = "playlistId"
        const val NAV_ARG_PLAYLIST_NAME = "playlistName"
        const val NAV_ARG_ENCODED_IMAGE_URL_STRING = "encodedImageUrlString"
        
        fun buildRoute(playlistSearchResult: SearchResult.PlaylistSearchResult): String {
            val routeWithoutUrl = "MusifyNavigationDestinations.AlbumDetailScreen" +
                    "/${playlistSearchResult.id}" +
                    "/${playlistSearchResult.name}"
            if (playlistSearchResult.imageUrlString == null) return routeWithoutUrl
            val encodedImageUrl = URLEncoder.encode(
                playlistSearchResult.imageUrlString,
                StandardCharsets.UTF_8.toString()
            )
            return "$routeWithoutUrl?encodedImageUrlString=$encodedImageUrl"
        }
    }
}