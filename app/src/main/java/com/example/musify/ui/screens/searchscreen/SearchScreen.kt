package com.example.musify.ui.screens.searchscreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.ExperimentalComposeUiApi
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
import com.example.musify.ui.components.DefaultMusifyErrorMessage
import com.example.musify.ui.components.DefaultMusifyLoadingAnimation
import com.example.musify.ui.components.FilterChip
import com.example.musify.ui.components.GenreCard
import com.example.musify.viewmodels.searchviewmodel.SearchFilter
import com.google.accompanist.insets.imePadding
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch

// fix lazy list scrolling to top after config change
@ExperimentalAnimationApi
@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun SearchScreen(
    genreList: List<Genre>,
    searchScreenFilters: List<SearchFilter>,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    currentlySelectedFilter: SearchFilter,
    onSearchFilterChanged: (SearchFilter) -> Unit,
    onGenreItemClick: (Genre) -> Unit,
    onSearchTextChanged: (searchText: String) -> Unit,
    isLoading: Boolean,
    isSearchErrorMessageVisible: Boolean,
    albumListForSearchQuery: LazyPagingItems<SearchResult.AlbumSearchResult>,
    artistListForSearchQuery: LazyPagingItems<SearchResult.ArtistSearchResult>,
    tracksListForSearchQuery: LazyPagingItems<SearchResult.TrackSearchResult>,
    playlistListForSearchQuery: LazyPagingItems<SearchResult.PlaylistSearchResult>,
    onSearchQueryItemClicked: (SearchResult) -> Unit,
    onImeDoneButtonClicked: KeyboardActionScope.(searchText: String) -> Unit,
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var isSearchListVisible by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val isSearchItemLoadingPlaceholderVisibleMap = remember {
        mutableStateMapOf<SearchResult, Boolean>()
    }
    val isFilterChipGroupVisible by remember { derivedStateOf { isSearchListVisible } }
    val coroutineScope = rememberCoroutineScope()
    BackHandler(isSearchListVisible) {
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
                    this,
                    searchText
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
        AnimatedContent(
            targetState = isSearchListVisible,
            transitionSpec = { fadeIn() with fadeOut() }
        ) { targetState ->
            when (targetState) {
                true -> SearchQueryList(
                    albumListForSearchQuery = albumListForSearchQuery,
                    artistListForSearchQuery = artistListForSearchQuery,
                    tracksListForSearchQuery = tracksListForSearchQuery,
                    playlistListForSearchQuery = playlistListForSearchQuery,
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
                    isSearchErrorMessageVisible = isSearchErrorMessageVisible
                )

                false -> LazyVerticalGrid(
                    modifier = Modifier
                        .background(MaterialTheme.colors.background)
                        .padding(top = 16.dp),
                    columns = GridCells.Adaptive(170.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    item(span = { GridItemSpan(this.maxCurrentLineSpan) }) {
                        Text(
                            text = "Genres",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                    items(items = genreList) {
                        GenreCard(
                            genre = it,
                            modifier = Modifier.height(120.dp),
                            onClick = { onGenreItemClick(it) },
                            imageResourceId = it.genreType.getAssociatedImageResource(),
                            backgroundColor = it.genreType.getAssociatedBackgroundColor()
                        )
                    }
                    item(span = { GridItemSpan(this.maxCurrentLineSpan) }) {
                        Spacer(modifier = Modifier.navigationBarsHeight())
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun SearchQueryList(
    albumListForSearchQuery: LazyPagingItems<SearchResult.AlbumSearchResult>,
    artistListForSearchQuery: LazyPagingItems<SearchResult.ArtistSearchResult>,
    tracksListForSearchQuery: LazyPagingItems<SearchResult.TrackSearchResult>,
    playlistListForSearchQuery: LazyPagingItems<SearchResult.PlaylistSearchResult>,
    onItemClick: (SearchResult) -> Unit,
    currentlySelectedFilter: SearchFilter,
    isLoadingPlaceholderVisible: (SearchResult) -> Boolean,
    onImageLoading: (SearchResult) -> Unit,
    onImageLoadingFinished: (SearchResult, Throwable?) -> Unit,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    lazyListState: LazyListState = rememberLazyListState(),
    isSearchResultsLoadingAnimationVisible: Boolean = false,
    isSearchErrorMessageVisible: Boolean = false,
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
                    .imePadding()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .background(MaterialTheme.colors.background.copy(alpha = 0.7f))
                    .fillMaxSize(),
                state = lazyListState,
            ) {
                when (currentlySelectedFilter) {
                    SearchFilter.ALBUMS -> searchAlbumListItems(
                        albumListForSearchQuery = albumListForSearchQuery,
                        onItemClick = onItemClick,
                        isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
                        onImageLoading = onImageLoading,
                        onImageLoadingFinished = onImageLoadingFinished
                    )
                    SearchFilter.TRACKS -> searchTrackListItems(
                        tracksListForSearchQuery = tracksListForSearchQuery,
                        onItemClick = onItemClick,
                        isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
                        onImageLoading = onImageLoading,
                        onImageLoadingFinished = onImageLoadingFinished,
                        currentlyPlayingTrack = currentlyPlayingTrack
                    )
                    SearchFilter.ARTISTS -> searchArtistListItems(
                        artistListForSearchQuery = artistListForSearchQuery,
                        onItemClick = onItemClick,
                        isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
                        onImageLoading = onImageLoading,
                        onImageLoadingFinished = onImageLoadingFinished,
                        artistImageErrorPainter = artistImageErrorPainter
                    )
                    SearchFilter.PLAYLISTS -> searchPlaylistListItems(
                        playlistListForSearchQuery = playlistListForSearchQuery,
                        onItemClick = onItemClick,
                        isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
                        onImageLoading = onImageLoading,
                        onImageLoadingFinished = onImageLoadingFinished,
                        playlistImageErrorPainter = playlistImageErrorPainter
                    )
                }
                item {
                    Spacer(modifier = Modifier.navigationBarsHeight())
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

        Spacer(modifier = Modifier.width(startPadding))
        filters.forEach {
            FilterChip(
                text = it.filterLabel,
                onClick = { onFilterClicked(it) },
                isSelected = it == currentlySelectedFilter
            )
        }
        Spacer(modifier = Modifier.width(endPadding))
    }
}

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
        AnimatedVisibility(
            visible = isClearSearchTextButtonVisible,
            enter = fadeIn() + slideInHorizontally { it },
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            IconButton(
                onClick = onCloseTextFieldButtonClicked,
                content = { Icon(imageVector = Icons.Filled.Close, contentDescription = null) }
            )
        }
    }
    val filterChipGroupScrollState = rememberScrollState()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            },
            trailingIcon = textFieldTrailingIcon,
            placeholder = {
                Text(
                    text = "Artists, songs, or podcasts",
                    fontWeight = FontWeight.SemiBold
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