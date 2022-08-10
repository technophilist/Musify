package com.example.musify.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.musify.domain.SearchResult
import com.example.musify.ui.screens.searchscreen.SearchScreen
import com.example.musify.viewmodels.searchviewmodel.SearchFilter
import com.example.musify.viewmodels.searchviewmodel.SearchScreenUiState
import com.example.musify.viewmodels.searchviewmodel.SearchViewModel

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
fun NavGraphBuilder.searchScreen(route: String) {
    composable(route = route) {
        val viewModel = hiltViewModel<SearchViewModel>()
        val albums = viewModel.albumListForSearchQuery.collectAsLazyPagingItems()
        val artists = viewModel.artistListForSearchQuery.collectAsLazyPagingItems()
        val playlists = viewModel.playlistListForSearchQuery.collectAsLazyPagingItems()
        val tracks = viewModel.trackListForSearchQuery.collectAsLazyPagingItems()
        val uiState by viewModel.uiState
        val isLoadingError by remember {
            derivedStateOf {
                tracks.loadState.refresh is LoadState.Error || tracks.loadState.append is LoadState.Error || tracks.loadState.prepend is LoadState.Error
            }
        }
        val controller = LocalSoftwareKeyboardController.current
        val genres = remember { viewModel.getAvailableGenres() }
        val filters = remember { SearchFilter.values().toList() }
        SearchScreen(
            genreList = genres,
            searchScreenFilters = filters,
            onGenreItemClick = {},
            onSearchTextChanged = viewModel::search,
            isSearchResultLoading = uiState == SearchScreenUiState.LOADING,
            albumListForSearchQuery = albums,
            artistListForSearchQuery = artists,
            tracksListForSearchQuery = tracks,
            playlistListForSearchQuery = playlists,
            onSearchQueryItemClicked = {
                if (it is SearchResult.TrackSearchResult) viewModel.playTrack(it)
            },
            currentlySelectedFilter = viewModel.currentlySelectedFilter.value,
            onSearchFilterChanged = viewModel::updateSearchFilter,
            isSearchErrorMessageVisible = isLoadingError,
            onImeDoneButtonClicked = {
                // FIXME search results start loading immediately when the search text is changed.
                //  Since the keyboard action is also displayed, the use might click the
                // search button as well, which will cause the search to repeat twice
                // wasting bandwidth
                viewModel.search(it)
                controller?.hide()
            }
        )
    }
}