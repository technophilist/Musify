package com.example.musify.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.musify.domain.SearchResult
import com.example.musify.ui.navigation.*

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun MusifyNavigation(
    navController: NavHostController,
    playTrack: (SearchResult.TrackSearchResult) -> Unit,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    isPlaybackLoading: Boolean,
    isFullScreenNowPlayingOverlayScreenVisible: Boolean,
) {
    val currentBackStack = navController.currentBackStackEntryAsState()
    val onBackButtonClicked = {
        if (currentBackStack.value?.destination?.route != MusifyNavigationDestinations.SearchScreen.route) {
            navController.popBackStack()
        }
        // don't pop backstack after reaching the search screen
    }
    NavHost(
        navController = navController,
        startDestination = MusifyNavigationDestinations.HomeScreen.route
    ) {
        homeScreen(
            route = MusifyNavigationDestinations.HomeScreen.route,
            onCarouselCardClicked = {
                navController.navigateBasedOnSearchResult(
                    searchResult = it.associatedSearchResult,
                    blockForTrackSearchResult = { track -> playTrack(track) }
                )
            }
        )
        searchScreen(
            route = MusifyNavigationDestinations.SearchScreen.route,
            currentlyPlayingTrack = currentlyPlayingTrack,
            isPlaybackLoading = isPlaybackLoading,
            onSearchResultClicked = {
                navController.navigateBasedOnSearchResult(
                    searchResult = it,
                    blockForTrackSearchResult = { track -> playTrack(track) }
                )
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

private fun NavHostController.navigateBasedOnSearchResult(
    searchResult: SearchResult,
    blockForTrackSearchResult: (SearchResult.TrackSearchResult) -> Unit
) {
    when (searchResult) {
        is SearchResult.AlbumSearchResult -> navigate(
            MusifyNavigationDestinations.AlbumDetailScreen.buildRoute(
                searchResult
            )
        ) {
            launchSingleTop = true
        }
        is SearchResult.ArtistSearchResult -> navigate(
            MusifyNavigationDestinations.ArtistDetailScreen.buildRoute(
                searchResult
            )
        ) {
            launchSingleTop = true
        }
        is SearchResult.PlaylistSearchResult -> navigate(
            MusifyNavigationDestinations.PlaylistDetailScreen.buildRoute(
                searchResult
            )
        ) {
            launchSingleTop = true
        }
        is SearchResult.TrackSearchResult -> {
            blockForTrackSearchResult(searchResult)
        }
    }
}