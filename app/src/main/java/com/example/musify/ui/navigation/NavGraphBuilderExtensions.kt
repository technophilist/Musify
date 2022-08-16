package com.example.musify.ui.navigation

import android.util.Base64
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
import com.example.musify.ui.screens.ArtistDetailScreen
import com.example.musify.ui.screens.searchscreen.SearchScreen
import com.example.musify.viewmodels.artistviewmodel.ArtistDetailViewModel
import com.example.musify.viewmodels.searchviewmodel.SearchFilter
import com.example.musify.viewmodels.searchviewmodel.SearchScreenUiState
import com.example.musify.viewmodels.searchviewmodel.SearchViewModel
import java.nio.charset.Charset

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
fun NavGraphBuilder.searchScreen(
    route: String,
    onArtistSearchResultClicked: (SearchResult.ArtistSearchResult) -> Unit
) {
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
                if (it is SearchResult.ArtistSearchResult) onArtistSearchResultClicked(it)
            },
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
            }
        )
    }
}

@ExperimentalMaterialApi
fun NavGraphBuilder.artistDetailScreen(
    route: String,
    onBackButtonClicked: () -> Unit,
    onAlbumClicked: (SearchResult.AlbumSearchResult) -> Unit
) {
    composable(route) { backStackEntry ->
        val viewModel = hiltViewModel<ArtistDetailViewModel>(backStackEntry)
        val artistName =
            backStackEntry.arguments!!.getString(MusifyNavigationDestinations.ArtistDetailScreen.NAV_ARG_ARTIST_NAME)!!
        val decodedUrlStringByteArray =
            backStackEntry.arguments!!.getString(MusifyNavigationDestinations.ArtistDetailScreen.NAV_ARG_ENCODED_IMAGE_URL_STRING)!!
                .run { Base64.decode(this, Base64.NO_WRAP) }
        val artistImageUrlString = String(decodedUrlStringByteArray, Charset.forName("US-ASCII"))
        ArtistDetailScreen(
            artistName = artistName,
            artistImageUrlString = artistImageUrlString,
            popularTracks = viewModel.popularTracks.value,
            releases = viewModel.albumsOfArtistFlow.collectAsLazyPagingItems(),
            onBackButtonClicked = onBackButtonClicked,
            onPlayButtonClicked = { /*TODO*/ },
            onTrackClicked = {},
            onAlbumClicked = onAlbumClicked
        )
    }
}