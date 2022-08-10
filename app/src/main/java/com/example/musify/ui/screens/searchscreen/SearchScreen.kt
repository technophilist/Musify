package com.example.musify.ui.screens.searchscreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.musify.R
import com.example.musify.domain.Genre
import com.example.musify.domain.SearchResult
import com.example.musify.ui.components.FilterChip
import com.example.musify.ui.components.GenreCard
import com.example.musify.viewmodels.searchviewmodel.SearchFilter
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch

// FIXME launching the app takes a while because of loading thumbnails of genres
// fix lazy list scrolling to top after config change
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun SearchScreen(
    genreList: List<Genre>,
    searchScreenFilters: List<SearchFilter>,
    currentlySelectedFilter: SearchFilter,
    onSearchFilterChanged: (SearchFilter) -> Unit,
    onGenreItemClick: (Genre) -> Unit,
    onSearchTextChanged: (searchText: String) -> Unit,
    isSearchResultLoading: Boolean,
    isSearchErrorMessageVisible: Boolean,
    albumListForSearchQuery: LazyPagingItems<SearchResult.AlbumSearchResult>,
    artistListForSearchQuery: LazyPagingItems<SearchResult.ArtistSearchResult>,
    tracksListForSearchQuery: LazyPagingItems<SearchResult.TrackSearchResult>,
    playlistListForSearchQuery: LazyPagingItems<SearchResult.PlaylistSearchResult>,
    onSearchQueryItemClicked: (SearchResult) -> Unit,
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var isSearchListVisible by rememberSaveable { mutableStateOf(false) }
    val isClearSearchTextButtonVisible by remember { derivedStateOf { isSearchListVisible && searchText.isNotEmpty() } }
    val focusManager = LocalFocusManager.current
    val textFieldTrailingIcon = @Composable {
        AnimatedVisibility(
            visible = isClearSearchTextButtonVisible,
            enter = fadeIn() + slideInHorizontally { it },
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            IconButton(
                onClick = {
                    searchText = ""
                    // notify the caller that the search text is empty
                    onSearchTextChanged("")
                },
                content = { Icon(imageVector = Icons.Filled.Close, contentDescription = null) }
            )
        }
    }
    val isSearchItemLoadingPlaceholderVisibleMap = remember {
        mutableStateMapOf<SearchResult, Boolean>()
    }
    val searchResultsLoadingAnimationComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.lottie_loading_anim)
    )
    val isFilterChipGroupVisible by remember { derivedStateOf { isSearchListVisible } }
    // Using separate horizontal padding modifier because the filter
    // group should be edge to edge. Adding a padding to the parent
    // composable will not allow the filter group to span to the edges.
    val horizontalPaddingModifier = Modifier.padding(horizontal = 16.dp)
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
    val coroutineScope = rememberCoroutineScope()
    val filterChipGroupScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = horizontalPaddingModifier,
            text = "Search",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h5
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .then(horizontalPaddingModifier)
                .onFocusChanged {
                    if (it.isFocused) {
                        isSearchListVisible = true
                    }
                },
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
            onValueChange = {
                searchText = it
                onSearchTextChanged(it)
            },
            textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                leadingIconColor = Color.Black,
                trailingIconColor = Color.Black,
                placeholderColor = Color.Black,
                textColor = Color.Black
            )
        )
        AnimatedVisibility(visible = isFilterChipGroupVisible) {
            FilterChipGroup(
                scrollState = filterChipGroupScrollState,
                filters = searchScreenFilters,
                currentlySelectedFilter = currentlySelectedFilter,
                onFilterClicked = {
                    onSearchFilterChanged(it)
                    coroutineScope.launch { lazyListState.animateScrollToItem(0) }
                }
            )
        }

        Box(modifier = horizontalPaddingModifier) {
            LazyVerticalGrid(
                cells = GridCells.Adaptive(170.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
            androidx.compose.animation.AnimatedVisibility(
                visible = isSearchListVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SearchQueryList(
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
                    onImageLoading = { isSearchItemLoadingPlaceholderVisibleMap[it] = true },
                    isSearchResultsLoadingAnimationVisible = isSearchResultLoading,
                    lottieComposition = searchResultsLoadingAnimationComposition,
                    lazyListState = lazyListState,
                    currentlySelectedFilter = currentlySelectedFilter,
                    isSearchErrorMessageVisible = isSearchErrorMessageVisible
                )
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
    lottieComposition: LottieComposition?,
    lazyListState: LazyListState = rememberLazyListState(),
    isSearchResultsLoadingAnimationVisible: Boolean = false,
    isSearchErrorMessageVisible: Boolean = false,
) {
    val artistImageErrorPainter =
        rememberVectorPainter(ImageVector.vectorResource(id = R.drawable.ic_outline_account_circle_24))
    val playlistImageErrorPainter =
        rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_outline_music_note_24))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        if (isSearchErrorMessageVisible) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .navigationBarsWithImePadding()
                    .padding(bottom = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Oops! Something doesn't look right",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Please check the internet connection",
                    style = MaterialTheme.typography.subtitle2
                )
            }

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                verticalArrangement = Arrangement.spacedBy(16.dp),
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
                        onImageLoadingFinished = onImageLoadingFinished
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
        AnimatedVisibility(
            modifier = Modifier
                .padding(16.dp)
                .size(128.dp) // the actual size will be mush lesser because of padding and offset
                .align(Alignment.Center)
                .offset(y = (-100).dp)
                .clip(RoundedCornerShape(5))
                .background(Color.White.copy(alpha = 0.1f))
                .padding(16.dp),
            visible = isSearchResultsLoadingAnimationVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LottieAnimation(
                composition = lottieComposition,
                iterations = LottieConstants.IterateForever
            )
        }
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