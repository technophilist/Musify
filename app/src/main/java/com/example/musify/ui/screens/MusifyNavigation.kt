package com.example.musify.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    NavHost(
        navController = navController,
        startDestination = MusifyBottomNavigationDestinations.Home.route
    ) {
        composable(MusifyBottomNavigationDestinations.Home.route) {
            val homeScreenNavController = rememberNavController()
            NavHostWithDetailScreens(
                navController = homeScreenNavController,
                startDestination = MusifyNavigationDestinations.HomeScreen.route,
                playTrack = playTrack,
                currentlyPlayingTrack = currentlyPlayingTrack,
                isPlaybackLoading = isPlaybackLoading
            ) {
                homeScreen(
                    route = MusifyNavigationDestinations.HomeScreen.route,
                    onCarouselCardClicked = {
                        homeScreenNavController.navigateBasedOnSearchResult(
                            searchResult = it.associatedSearchResult,
                            blockForTrackSearchResult = { track -> playTrack(track) }
                        )
                    }
                )
            }
        }

        composable(MusifyBottomNavigationDestinations.Search.route) {
            val searchScreenNavController = rememberNavController()
            NavHostWithDetailScreens(
                navController = searchScreenNavController,
                startDestination = MusifyNavigationDestinations.SearchScreen.route,
                playTrack = playTrack,
                currentlyPlayingTrack = currentlyPlayingTrack,
                isPlaybackLoading = isPlaybackLoading
            ) {
                searchScreen(
                    route = MusifyNavigationDestinations.SearchScreen.route,
                    currentlyPlayingTrack = currentlyPlayingTrack,
                    isPlaybackLoading = isPlaybackLoading,
                    onSearchResultClicked = {
                        searchScreenNavController.navigateBasedOnSearchResult(
                            searchResult = it,
                            blockForTrackSearchResult = { track -> playTrack(track) }
                        )
                    },
                    isFullScreenNowPlayingScreenOverlayVisible = isFullScreenNowPlayingOverlayScreenVisible
                )
            }
        }
    }
}

private fun NavHostController.navigateBasedOnSearchResult(
    searchResult: SearchResult,
    blockForTrackSearchResult: (SearchResult.TrackSearchResult) -> Unit
) {
    when (searchResult) {
        is SearchResult.AlbumSearchResult -> navigate(
            MusifyNavigationDestinations
                .AlbumDetailScreen
                .buildRoute(searchResult)
        ) { launchSingleTop = true }

        is SearchResult.ArtistSearchResult -> navigate(
            MusifyNavigationDestinations
                .ArtistDetailScreen
                .buildRoute(searchResult)
        ) { launchSingleTop = true }

        is SearchResult.PlaylistSearchResult -> navigate(
            MusifyNavigationDestinations
                .PlaylistDetailScreen
                .buildRoute(searchResult)
        ) { launchSingleTop = true }

        is SearchResult.TrackSearchResult -> {
            blockForTrackSearchResult(searchResult)
        }
    }
}