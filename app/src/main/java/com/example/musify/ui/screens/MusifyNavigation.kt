package com.example.musify.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musify.domain.SearchResult
import com.example.musify.ui.navigation.*

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun MusifyNavigation(
    playTrack: (SearchResult.TrackSearchResult) -> Unit,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    isPlaybackLoading: Boolean,
    isFullScreenNowPlayingOverlayScreenVisible:Boolean,
) {
    val navController = rememberNavController()
    val currentBackStack = navController.currentBackStackEntryAsState()
    val onBackButtonClicked = {
        if (currentBackStack.value?.destination?.route != MusifyNavigationDestinations.SearchScreen.route) {
            navController.popBackStack()
        }
        // don't pop backstack after reaching the search screen
    }
    NavHost(
        navController = navController,
        startDestination = MusifyNavigationDestinations.SearchScreen.route
    ) {
        searchScreen(
            route = MusifyNavigationDestinations.SearchScreen.route,
            currentlyPlayingTrack = currentlyPlayingTrack,
            isPlaybackLoading = isPlaybackLoading,
            onSearchResultClicked = {
                when (it) {
                    is SearchResult.AlbumSearchResult -> navController
                        .navigate(MusifyNavigationDestinations.AlbumDetailScreen.buildRoute(it)) {
                            launchSingleTop = true
                        }
                    is SearchResult.ArtistSearchResult -> navController
                        .navigate(MusifyNavigationDestinations.ArtistDetailScreen.buildRoute(it)) {
                            launchSingleTop = true
                        }
                    is SearchResult.PlaylistSearchResult -> navController
                        .navigate(MusifyNavigationDestinations.PlaylistDetailScreen.buildRoute(it)) {
                            launchSingleTop = true
                        }
                    is SearchResult.TrackSearchResult -> playTrack(it)
                }
            },
            isFullScreenNowPlayingScreenOverlayVisible = isFullScreenNowPlayingOverlayScreenVisible
        )
        artistDetailScreen(
            route = MusifyNavigationDestinations.ArtistDetailScreen.route,
            arguments = listOf(
                navArgument(MusifyNavigationDestinations.ArtistDetailScreen.NAV_ARG_ENCODED_IMAGE_URL_STRING) {
                    nullable = true
                }
            ),
            onBackButtonClicked = onBackButtonClicked,
            onAlbumClicked = {
                navController.navigate(MusifyNavigationDestinations.AlbumDetailScreen.buildRoute(it)) {
                    launchSingleTop = true
                }
            },
            onPlayTrack = playTrack,
            currentlyPlayingTrack = currentlyPlayingTrack,
            isPlaybackLoading = isPlaybackLoading,
        )
        albumDetailScreen(
            route = MusifyNavigationDestinations.AlbumDetailScreen.route,
            onBackButtonClicked = onBackButtonClicked,
            onPlayTrack = playTrack,
            currentlyPlayingTrack = currentlyPlayingTrack,
            isPlaybackLoading = isPlaybackLoading
        )

        playlistDetailScreen(
            route = MusifyNavigationDestinations.PlaylistDetailScreen.route,
            onBackButtonClicked = onBackButtonClicked,
            onPlayTrack = playTrack,
            currentlyPlayingTrack = currentlyPlayingTrack,
            isPlaybackLoading = isPlaybackLoading
        )
    }
}