package com.example.musify.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.musify.domain.HomeFeedCarouselCardInfo
import com.example.musify.domain.HomeFeedFilters
import com.example.musify.domain.SearchResult
import com.example.musify.ui.screens.GetPremiumScreen
import com.example.musify.ui.screens.homescreen.HomeScreen
import com.example.musify.ui.screens.searchscreen.PagingItemsForSearchScreen
import com.example.musify.ui.screens.searchscreen.SearchScreen
import com.example.musify.ui.theme.dynamictheme.DynamicBackgroundType
import com.example.musify.ui.theme.dynamictheme.DynamicThemeResource
import com.example.musify.ui.theme.dynamictheme.DynamicallyThemedSurface
import com.example.musify.viewmodels.homefeedviewmodel.HomeFeedViewModel
import com.example.musify.viewmodels.searchviewmodel.SearchFilter
import com.example.musify.viewmodels.searchviewmodel.SearchScreenUiState
import com.example.musify.viewmodels.searchviewmodel.SearchViewModel

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
        navGraphWithDetailScreens(
            navGraphRoute = MusifyBottomNavigationDestinations.Home.route,
            startDestination = MusifyNavigationDestinations.HomeScreen.route,
            navController = navController,
            playTrack = playTrack,
            currentlyPlayingTrack = currentlyPlayingTrack,
            isPlaybackLoading = isPlaybackLoading
        ) { nestedController ->
            homeScreen(
                route = MusifyNavigationDestinations.HomeScreen.route,
                onCarouselCardClicked = {
                    nestedController.navigateToDetailScreen(searchResult = it.associatedSearchResult)
                }
            )
        }
        navGraphWithDetailScreens(
            navGraphRoute = MusifyBottomNavigationDestinations.Search.route,
            startDestination = MusifyNavigationDestinations.SearchScreen.route,
            navController = navController,
            playTrack = playTrack,
            currentlyPlayingTrack = currentlyPlayingTrack,
            isPlaybackLoading = isPlaybackLoading
        ) { nestedController ->
            searchScreen(
                route = MusifyNavigationDestinations.SearchScreen.route,
                currentlyPlayingTrack = currentlyPlayingTrack,
                isPlaybackLoading = isPlaybackLoading,
                onSearchResultClicked = nestedController::navigateToDetailScreen,
                isFullScreenNowPlayingScreenOverlayVisible = isFullScreenNowPlayingOverlayScreenVisible
            )
        }

        composable(MusifyBottomNavigationDestinations.Premium.route) {
            GetPremiumScreen()
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
private fun NavGraphBuilder.homeScreen(
    route: String,
    onCarouselCardClicked: (HomeFeedCarouselCardInfo) -> Unit
) {
    composable(route) {
        val homeFeedViewModel = hiltViewModel<HomeFeedViewModel>()
        val filters = remember {
            listOf(
                HomeFeedFilters.Music,
                HomeFeedFilters.PodcastsAndShows
            )
        }
        HomeScreen(
            timeBasedGreeting = homeFeedViewModel.greetingPhrase,
            homeFeedFilters = filters,
            currentlySelectedHomeFeedFilter = HomeFeedFilters.None,
            onHomeFeedFilterClick = {},
            carousels = homeFeedViewModel.homeFeedCarousels.value,
            onHomeFeedCarouselCardClick = onCarouselCardClicked,
            isErrorMessageVisible = homeFeedViewModel.uiState.value == HomeFeedViewModel.HomeFeedUiState.ERROR,
            isLoading = homeFeedViewModel.uiState.value == HomeFeedViewModel.HomeFeedUiState.LOADING,
            onErrorRetryButtonClick = homeFeedViewModel::refreshFeed
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
private fun NavGraphBuilder.searchScreen(
    route: String,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    isPlaybackLoading: Boolean,
    onSearchResultClicked: (SearchResult) -> Unit,
    isFullScreenNowPlayingScreenOverlayVisible: Boolean,
) {
    composable(route = route) {
        val viewModel = hiltViewModel<SearchViewModel>()
        val albums = viewModel.albumListForSearchQuery.collectAsLazyPagingItems()
        val artists = viewModel.artistListForSearchQuery.collectAsLazyPagingItems()
        val playlists = viewModel.playlistListForSearchQuery.collectAsLazyPagingItems()
        val tracks = viewModel.trackListForSearchQuery.collectAsLazyPagingItems()
        val pagingItems = remember {
            PagingItemsForSearchScreen(
                albums,
                artists,
                tracks,
                playlists
            )
        }
        val uiState by viewModel.uiState
        val isLoadingError by remember {
            derivedStateOf {
                tracks.loadState.refresh is LoadState.Error || tracks.loadState.append is LoadState.Error || tracks.loadState.prepend is LoadState.Error
            }
        }
        val controller = LocalSoftwareKeyboardController.current
        val genres = remember { viewModel.getAvailableGenres() }
        val filters = remember { SearchFilter.values().toList() }
        val currentlySelectedFilter by viewModel.currentlySelectedFilter
        val dynamicThemeResource by remember {
            derivedStateOf {
                val imageUrl = when (currentlySelectedFilter) {
                    SearchFilter.ALBUMS -> albums.itemSnapshotList.firstOrNull()?.albumArtUrlString
                    SearchFilter.TRACKS -> tracks.itemSnapshotList.firstOrNull()?.imageUrlString
                    SearchFilter.ARTISTS -> artists.itemSnapshotList.firstOrNull()?.imageUrlString
                    SearchFilter.PLAYLISTS -> playlists.itemSnapshotList.firstOrNull()?.imageUrlString
                }
                if (imageUrl == null) DynamicThemeResource.Empty
                else DynamicThemeResource.FromImageUrl(imageUrl)
            }
        }
        DynamicallyThemedSurface(
            dynamicThemeResource = dynamicThemeResource,
            dynamicBackgroundType = DynamicBackgroundType.Gradient()
        ) {
            SearchScreen(
                genreList = genres,
                searchScreenFilters = filters,
                onGenreItemClick = {},
                onSearchTextChanged = viewModel::search,
                isLoading = uiState == SearchScreenUiState.LOADING || isPlaybackLoading,
                pagingItems = pagingItems,
                onSearchQueryItemClicked = onSearchResultClicked,
                currentlySelectedFilter = viewModel.currentlySelectedFilter.value,
                onSearchFilterChanged = viewModel::updateSearchFilter,
                isSearchErrorMessageVisible = isLoadingError,
                onImeDoneButtonClicked = {
                    // Search only if there was an error while loading.
                    // A manual call to search() is not required
                    // when there is no error because, search()
                    // will be called automatically, everytime the
                    // search text changes. This prevents duplicate
                    // calls when the user manually clicks the done
                    // button after typing the search text, in
                    // which case, the keyboard will just be hidden.
                    if (isLoadingError) viewModel.search(it)
                    controller?.hide()
                },
                currentlyPlayingTrack = currentlyPlayingTrack,
                isFullScreenNowPlayingOverlayScreenVisible = isFullScreenNowPlayingScreenOverlayVisible,
                onErrorRetryButtonClick = viewModel::search
            )
        }
    }
}
