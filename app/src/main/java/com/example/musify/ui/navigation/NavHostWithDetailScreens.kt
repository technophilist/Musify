package com.example.musify.ui.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.musify.R
import com.example.musify.domain.SearchResult
import com.example.musify.ui.screens.AlbumDetailScreen
import com.example.musify.ui.screens.ArtistDetailScreen
import com.example.musify.ui.screens.PlaylistDetailScreen
import com.example.musify.ui.theme.dynamictheme.DynamicBackgroundType
import com.example.musify.ui.theme.dynamictheme.DynamicThemeResource
import com.example.musify.ui.theme.dynamictheme.DynamicallyThemedSurface
import com.example.musify.viewmodels.AlbumDetailUiState
import com.example.musify.viewmodels.AlbumDetailViewModel
import com.example.musify.viewmodels.PlaylistDetailViewModel
import com.example.musify.viewmodels.artistviewmodel.ArtistDetailScreenUiState
import com.example.musify.viewmodels.artistviewmodel.ArtistDetailViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

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
    modifier: Modifier = Modifier,
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

@ExperimentalMaterialApi
private fun NavGraphBuilder.artistDetailScreen(
    route: String,
    onBackButtonClicked: () -> Unit,
    onPlayTrack: (SearchResult.TrackSearchResult) -> Unit,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    isPlaybackLoading: Boolean,
    onAlbumClicked: (SearchResult.AlbumSearchResult) -> Unit,
    arguments: List<NamedNavArgument> = emptyList()
) {
    composable(route, arguments) { backStackEntry ->
        val viewModel = hiltViewModel<ArtistDetailViewModel>(backStackEntry)
        val arguments = backStackEntry.arguments!!
        val artistName =
            arguments.getString(MusifyNavigationDestinations.ArtistDetailScreen.NAV_ARG_ARTIST_NAME)!!
        val artistImageUrlString =
            arguments.getString(MusifyNavigationDestinations.ArtistDetailScreen.NAV_ARG_ENCODED_IMAGE_URL_STRING)
                ?.run { URLDecoder.decode(this, StandardCharsets.UTF_8.toString()) }
        val releases = viewModel.albumsOfArtistFlow.collectAsLazyPagingItems()
        val uiState by viewModel.uiState
        ArtistDetailScreen(
            artistName = artistName,
            artistImageUrlString = artistImageUrlString,
            popularTracks = viewModel.popularTracks.value,
            releases = releases,
            currentlyPlayingTrack = currentlyPlayingTrack,
            onBackButtonClicked = onBackButtonClicked,
            onPlayButtonClicked = { /*TODO*/ },
            onTrackClicked = onPlayTrack,
            onAlbumClicked = onAlbumClicked,
            isLoading = uiState is ArtistDetailScreenUiState.Loading || isPlaybackLoading,
            fallbackImageRes = R.drawable.ic_outline_account_circle_24,
            isErrorMessageVisible = uiState is ArtistDetailScreenUiState.Error
        )
    }
}

@ExperimentalMaterialApi
private fun NavGraphBuilder.albumDetailScreen(
    route: String,
    onBackButtonClicked: () -> Unit,
    onPlayTrack: (SearchResult.TrackSearchResult) -> Unit,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    isPlaybackLoading: Boolean,
) {
    composable(route) { backStackEntry ->
        val arguments = backStackEntry.arguments!!
        val viewModel = hiltViewModel<AlbumDetailViewModel>()
        val albumArtUrl =
            arguments.getString(MusifyNavigationDestinations.AlbumDetailScreen.NAV_ARG_ENCODED_IMAGE_URL_STRING)!!
        val albumName =
            arguments.getString(MusifyNavigationDestinations.AlbumDetailScreen.NAV_ARG_ALBUM_NAME)!!
        val artists =
            arguments.getString(MusifyNavigationDestinations.AlbumDetailScreen.NAV_ARG_ARTISTS_STRING)!!
        val yearOfRelease =
            arguments.getString(MusifyNavigationDestinations.AlbumDetailScreen.NAV_ARG_YEAR_OF_RELEASE_STRING)!!
        DynamicallyThemedSurface(
            dynamicThemeResource = DynamicThemeResource.FromImageUrl(albumArtUrl),
            dynamicBackgroundType = DynamicBackgroundType.Gradient(fraction = 0.5f)
        ) {
            AlbumDetailScreen(
                albumName = albumName,
                artistsString = artists,
                yearOfRelease = yearOfRelease,
                albumArtUrlString = albumArtUrl,
                trackList = viewModel.tracks.value,
                onTrackItemClick = onPlayTrack,
                onBackButtonClicked = onBackButtonClicked,
                isLoading = isPlaybackLoading || viewModel.uiState.value is AlbumDetailUiState.Loading,
                isErrorMessageVisible = viewModel.uiState.value is AlbumDetailUiState.Error,
                currentlyPlayingTrack = currentlyPlayingTrack
            )
        }
    }
}

@ExperimentalMaterialApi
private fun NavGraphBuilder.playlistDetailScreen(
    route: String,
    onBackButtonClicked: () -> Unit,
    onPlayTrack: (SearchResult.TrackSearchResult) -> Unit,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    isPlaybackLoading: Boolean,
    navigationArguments: List<NamedNavArgument> = emptyList()
) {
    composable(route = route, arguments = navigationArguments) {
        val arguments = it.arguments!!
        val viewModel = hiltViewModel<PlaylistDetailViewModel>()
        val tracks = viewModel.tracks.collectAsLazyPagingItems()
        val playlistName =
            arguments.getString(MusifyNavigationDestinations.PlaylistDetailScreen.NAV_ARG_PLAYLIST_NAME)!!
        val imageUrlString =
            arguments.getString(MusifyNavigationDestinations.PlaylistDetailScreen.NAV_ARG_ENCODED_IMAGE_URL_STRING)!!
        val ownerName =
            arguments.getString(MusifyNavigationDestinations.PlaylistDetailScreen.NAV_ARG_OWNER_NAME)!!
        val totalNumberOfTracks =
            arguments.getString(MusifyNavigationDestinations.PlaylistDetailScreen.NAV_ARG_NUMBER_OF_TRACKS)!!
        val isErrorMessageVisible by remember {
            derivedStateOf {
                tracks.loadState.refresh is LoadState.Error ||
                        tracks.loadState.append is LoadState.Error ||
                        tracks.loadState.prepend is LoadState.Error

            }
        }
        DynamicallyThemedSurface(
            dynamicThemeResource = DynamicThemeResource.FromImageUrl(imageUrlString),
            dynamicBackgroundType = DynamicBackgroundType.Gradient(fraction = 0.5f)
        ) {
            PlaylistDetailScreen(
                playlistName = playlistName,
                playlistImageUrlString = imageUrlString,
                nameOfPlaylistOwner = ownerName,
                totalNumberOfTracks = totalNumberOfTracks,
                imageResToUseWhenImageUrlStringIsNull = R.drawable.ic_outline_account_circle_24, // TODO
                tracks = tracks,
                currentlyPlayingTrack = currentlyPlayingTrack,
                onBackButtonClicked = onBackButtonClicked,
                onTrackClicked = onPlayTrack,
                isLoading = tracks.loadState.refresh is LoadState.Loading || isPlaybackLoading,
                isErrorMessageVisible = isErrorMessageVisible
            )
        }
    }
}


