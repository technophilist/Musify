package com.example.musify.ui.screens.searchscreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.example.musify.R
import com.example.musify.domain.Genre
import com.example.musify.domain.SearchResult
import com.example.musify.ui.components.*
import com.example.musify.viewmodels.searchviewmodel.SearchFilter
import kotlinx.coroutines.launch

/**
 * A data class that contains all the different paging items associated
 * with the[SearchScreen].
 */
data class PagingItemsForSearchScreen(
    val albumListForSearchQuery: LazyPagingItems<SearchResult.AlbumSearchResult>,
    val artistListForSearchQuery: LazyPagingItems<SearchResult.ArtistSearchResult>,
    val tracksListForSearchQuery: LazyPagingItems<SearchResult.TrackSearchResult>,
    val playlistListForSearchQuery: LazyPagingItems<SearchResult.PlaylistSearchResult>,
    val podcastListForSearchQuery: LazyPagingItems<SearchResult.PodcastSearchResult>,
    val episodeListForSearchQuery: LazyPagingItems<SearchResult.EpisodeSearchResult>
)

// fix lazy list scrolling to top after config change
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun SearchScreen(
    genreList: List<Genre>,
    searchScreenFilters: List<SearchFilter>,
    pagingItems: PagingItemsForSearchScreen,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    currentlySelectedFilter: SearchFilter,
    onSearchFilterChanged: (SearchFilter) -> Unit,
    onGenreItemClick: (Genre) -> Unit,
    onSearchTextChanged: (searchText: String) -> Unit,
    onErrorRetryButtonClick: (searchQuery: String) -> Unit,
    isLoading: Boolean,
    isSearchErrorMessageVisible: Boolean,
    onSearchQueryItemClicked: (SearchResult) -> Unit,
    onImeDoneButtonClicked: KeyboardActionScope.(searchText: String) -> Unit,
    isFullScreenNowPlayingOverlayScreenVisible: Boolean
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var isSearchListVisible by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val isSearchItemLoadingPlaceholderVisibleMap = remember {
        mutableStateMapOf<SearchResult, Boolean>()
    }
    val isFilterChipGroupVisible by remember { derivedStateOf { isSearchListVisible } }
    val coroutineScope = rememberCoroutineScope()
    // If there are nested back handlers and both of them are enabled, then the
    // handler that is at the root of the nested hierarchy will consume the
    // back handler event. Hence, any back handlers declared at a higher level
    // will not be executed.
    // if the full screen player is visible, then don't enable this back handler
    // this will allow the caller to set a back handler that will close the player
    // before this back handler is executed.
    BackHandler(isSearchListVisible && !isFullScreenNowPlayingOverlayScreenVisible) {
        // remove focus on the search text field
        focusManager.clearFocus()
        if (searchText.isNotEmpty()) {
            searchText = ""
            // notify the caller that the text has been emptied out
            onSearchTextChanged(searchText)
        }
        isSearchListVisible = false
    }
    val lazyListState = rememberLazyListState()
    val searchBarBackgroundAlpha by remember(isSearchListVisible) {
        mutableStateOf(if (isSearchListVisible) 0.8f else 1f)
    }
    val searchBarAlpha by animateFloatAsState(targetValue = searchBarBackgroundAlpha)
    Column(modifier = Modifier.fillMaxWidth()) {
        SearchBarWithFilterChips(
            modifier = Modifier
                .background(MaterialTheme.colors.background.copy(alpha = searchBarAlpha))
                .statusBarsPadding()
                .padding(top = 16.dp),
            isFilterChipGroupVisible = isFilterChipGroupVisible,
            isSearchListVisible = isSearchListVisible,
            searchText = searchText,
            filters = searchScreenFilters,
            currentlySelectedFilter = currentlySelectedFilter,
            onCloseTextFieldButtonClicked = {
                searchText = ""
                // notify the caller that the search text is empty
                onSearchTextChanged("")
            },
            onImeDoneButtonClicked = {
                onImeDoneButtonClicked(
                    this, searchText
                )
            },
            onTextFieldFocusChanged = { if (it.isFocused) isSearchListVisible = true },
            onSearchTextChanged = {
                searchText = it
                onSearchTextChanged(it)
            },
            onFilterClicked = {
                onSearchFilterChanged(it)
                coroutineScope.launch { lazyListState.animateScrollToItem(0) }
            },
        )
        AnimatedContent(targetState = isSearchListVisible,
            transitionSpec = { fadeIn() with fadeOut() }) { targetState ->
            when (targetState) {
                true -> SearchQueryList(pagingItems = pagingItems,
                    onItemClick = { onSearchQueryItemClicked(it) },
                    isLoadingPlaceholderVisible = { item ->
                        isSearchItemLoadingPlaceholderVisibleMap.getOrPut(item) { false }
                    },
                    onImageLoadingFinished = { item, _ ->
                        isSearchItemLoadingPlaceholderVisibleMap[item] = false
                    },
                    onImageLoading = {
                        isSearchItemLoadingPlaceholderVisibleMap[it] = true
                    },
                    isSearchResultsLoadingAnimationVisible = isLoading,
                    currentlyPlayingTrack = currentlyPlayingTrack,
                    lazyListState = lazyListState,
                    currentlySelectedFilter = currentlySelectedFilter,
                    isSearchErrorMessageVisible = isSearchErrorMessageVisible,
                    onErrorRetryButtonClick = { onErrorRetryButtonClick(searchText) })

                false -> GenresGrid(
                    modifier = Modifier
                        .background(MaterialTheme.colors.background)
                        .padding(top = 16.dp),
                    availableGenres = genreList,
                    onGenreItemClick = onGenreItemClick
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun SearchQueryList(
    pagingItems: PagingItemsForSearchScreen,
    onItemClick: (SearchResult) -> Unit,
    currentlySelectedFilter: SearchFilter,
    isLoadingPlaceholderVisible: (SearchResult) -> Boolean,
    onImageLoading: (SearchResult) -> Unit,
    onImageLoadingFinished: (SearchResult, Throwable?) -> Unit,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    lazyListState: LazyListState = rememberLazyListState(),
    isSearchResultsLoadingAnimationVisible: Boolean = false,
    isSearchErrorMessageVisible: Boolean = false,
    onErrorRetryButtonClick: () -> Unit
) {
    val artistImageErrorPainter =
        rememberVectorPainter(ImageVector.vectorResource(id = R.drawable.ic_outline_account_circle_24))
    val playlistImageErrorPainter =
        rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_outline_music_note_24))

    Box(modifier = Modifier.fillMaxSize()) {
        if (isSearchErrorMessageVisible) {
            DefaultMusifyErrorMessage(
                title = "Oops! Something doesn't look right",
                subtitle = "Please check the internet connection",
                modifier = Modifier
                    .align(Alignment.Center)
                    .imePadding(),
                onRetryButtonClicked = onErrorRetryButtonClick
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .background(MaterialTheme.colors.background.copy(alpha = 0.7f))
                    .fillMaxSize(),
                state = lazyListState,
                contentPadding = PaddingValues(
                    bottom = MusifyBottomNavigationConstants.navigationHeight + MusifyMiniPlayerConstants.miniPlayerHeight
                ),
            ) {
                when (currentlySelectedFilter) {
                    SearchFilter.ALBUMS -> searchAlbumListItems(
                        albumListForSearchQuery = pagingItems.albumListForSearchQuery,
                        onItemClick = onItemClick,
                        isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
                        onImageLoading = onImageLoading,
                        onImageLoadingFinished = onImageLoadingFinished
                    )
                    SearchFilter.TRACKS -> searchTrackListItems(
                        tracksListForSearchQuery = pagingItems.tracksListForSearchQuery,
                        onItemClick = onItemClick,
                        isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
                        onImageLoading = onImageLoading,
                        onImageLoadingFinished = onImageLoadingFinished,
                        currentlyPlayingTrack = currentlyPlayingTrack
                    )
                    SearchFilter.ARTISTS -> searchArtistListItems(
                        artistListForSearchQuery = pagingItems.artistListForSearchQuery,
                        onItemClick = onItemClick,
                        isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
                        onImageLoading = onImageLoading,
                        onImageLoadingFinished = onImageLoadingFinished,
                        artistImageErrorPainter = artistImageErrorPainter
                    )
                    SearchFilter.PLAYLISTS -> searchPlaylistListItems(
                        playlistListForSearchQuery = pagingItems.playlistListForSearchQuery,
                        onItemClick = onItemClick,
                        isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
                        onImageLoading = onImageLoading,
                        onImageLoadingFinished = onImageLoadingFinished,
                        playlistImageErrorPainter = playlistImageErrorPainter
                    )
                    SearchFilter.PODCASTS -> searchPodcastListItems(podcastsForSearchQuery = pagingItems.podcastListForSearchQuery,
                        episodesForSearchQuery = pagingItems.episodeListForSearchQuery,
                        onPodcastItemClicked = { /*TODO*/ },
                        onEpisodeItemClicked = { /*TODO*/ }
                    )
                }
                item {
                    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
        }
        DefaultMusifyLoadingAnimation(
            modifier = Modifier
                .align(Alignment.Center)
                .imePadding(),
            isVisible = isSearchResultsLoadingAnimationVisible
        )
    }
}


@ExperimentalMaterialApi
@Composable
private fun FilterChipGroup(
    scrollState: ScrollState,
    filters: List<SearchFilter>,
    currentlySelectedFilter: SearchFilter,
    onFilterClicked: (SearchFilter) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp)
) {
    val currentLayoutDirection = LocalLayoutDirection.current
    val startPadding = contentPadding.calculateStartPadding(currentLayoutDirection)
    val endPadding = contentPadding.calculateEndPadding(currentLayoutDirection)
    Row(
        modifier = modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // There is no content padding param for row.
        // If the padding is applied directly on the row, then it'll
        // apply it to the entire row rather than applying it to
        // whatever is defined in it's content parameter.
        Spacer(modifier = Modifier.width(startPadding))
        filters.forEach {
            MusifyFilterChip(
                text = it.filterLabel,
                onClick = { onFilterClicked(it) },
                isSelected = it == currentlySelectedFilter
            )
        }
        Spacer(modifier = Modifier.width(endPadding))
    }
}

@ExperimentalMaterialApi
@Composable
private fun SearchBarWithFilterChips(
    searchText: String,
    isSearchListVisible: Boolean,
    isFilterChipGroupVisible: Boolean,
    filters: List<SearchFilter>,
    currentlySelectedFilter: SearchFilter,
    onSearchTextChanged: (String) -> Unit,
    onCloseTextFieldButtonClicked: () -> Unit,
    onTextFieldFocusChanged: (FocusState) -> Unit,
    onFilterClicked: (SearchFilter) -> Unit,
    onImeDoneButtonClicked: KeyboardActionScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    val isClearSearchTextButtonVisible by remember(isSearchListVisible, searchText) {
        mutableStateOf(isSearchListVisible && searchText.isNotEmpty())
    }
    val textFieldTrailingIcon = @Composable {
        AnimatedVisibility(visible = isClearSearchTextButtonVisible,
            enter = fadeIn() + slideInHorizontally { it },
            exit = slideOutHorizontally { it } + fadeOut()) {
            IconButton(onClick = onCloseTextFieldButtonClicked,
                content = { Icon(imageVector = Icons.Filled.Close, contentDescription = null) })
        }
    }
    val filterChipGroupScrollState = rememberScrollState()
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Search",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h5
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .onFocusChanged(onTextFieldFocusChanged),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search, contentDescription = null
                )
            },
            trailingIcon = textFieldTrailingIcon,
            placeholder = {
                Text(
                    text = "Artists, songs, or podcasts", fontWeight = FontWeight.SemiBold
                )
            },
            singleLine = true,
            value = searchText,
            onValueChange = onSearchTextChanged,
            textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                leadingIconColor = Color.Black,
                trailingIconColor = Color.Black,
                placeholderColor = Color.Black,
                textColor = Color.Black
            ),
            keyboardActions = KeyboardActions(onDone = onImeDoneButtonClicked)
        )
        AnimatedVisibility(visible = isFilterChipGroupVisible) {
            FilterChipGroup(
                scrollState = filterChipGroupScrollState,
                filters = filters,
                currentlySelectedFilter = currentlySelectedFilter,
                onFilterClicked = onFilterClicked
            )
        }
    }
}