package com.example.musify.ui.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.musify.domain.SearchResult

/**
 * A [NavHost] that already contains detail screens in it's navigation graph.
 * @param navController the navController for this host.
 * @param startDestination the route for the start destination.
 * @param playTrack lambda to execute when a track is to be played.
 * @param currentlyPlayingTrack indicates that currently playing track.
 * @param isPlaybackLoading indicates whether the playback is loading.
 * @param builder the builder used to construct the graph that is used to define
 * other navigation destinations.
 */
@ExperimentalMaterialApi
@Composable
fun NavHostWithDetailScreens(
    navController: NavHostController,
    startDestination: String,
    playTrack: (SearchResult.TrackSearchResult) -> Unit,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    isPlaybackLoading: Boolean,
    modifier:Modifier = Modifier,
    builder: NavGraphBuilder.() -> Unit
) {
    val currentBackStack = navController.currentBackStackEntryAsState()
    val onBackButtonClicked = {
        if (currentBackStack.value?.destination?.route != MusifyNavigationDestinations.SearchScreen.route) {
            navController.popBackStack()
        }
        // don't pop backstack after reaching the search screen
    }
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        builder()
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



