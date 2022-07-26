package com.example.musify.ui.screens

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
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.musify.R
import com.example.musify.domain.Genre
import com.example.musify.domain.SearchResult
import com.example.musify.domain.SearchResults
import com.example.musify.ui.components.FilterChip
import com.example.musify.ui.components.GenreCard
import com.example.musify.ui.components.ListItemCardType
import com.example.musify.ui.components.MusifyCompactListItemCard
import com.example.musify.viewmodels.searchviewmodel.SearchFilter
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun SearchScreen(
    genreList: List<Genre>,
    searchScreenFilters: List<SearchFilter>,
    onSearchFilterClicked: (SearchFilter) -> Unit,
    onGenreItemClick: (Genre) -> Unit,
    onSearchTextChanged: (searchText: String, filter: SearchFilter) -> Unit,
    isSearchResultLoading: Boolean,
    searchQueryResult: SearchResults,
    onSearchQueryItemClicked: (SearchResult) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var isSearchListVisible by remember { mutableStateOf(false) }
    val isClearSearchTextButtonVisible by remember { derivedStateOf { isSearchListVisible && searchText.isNotEmpty() } }
    val focusManager = LocalFocusManager.current
    var currentlySelectedSearchScreenFilter by remember { mutableStateOf(SearchFilter.TRACKS) }
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
                    onSearchTextChanged("", currentlySelectedSearchScreenFilter)
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
        if (searchText.isEmpty()) isSearchListVisible = false
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
                onSearchTextChanged(it, currentlySelectedSearchScreenFilter)
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
                currentlySelectedFilter = currentlySelectedSearchScreenFilter,
                onFilterClicked = {
                    currentlySelectedSearchScreenFilter = it
                    onSearchFilterClicked(it)
                    coroutineScope.launch { lazyListState.animateScrollToItem(0) }
                }
            )
        }
        Box(modifier = horizontalPaddingModifier) {
            Text(
                text = "Genres",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.subtitle1
            )

            LazyVerticalGrid(
                cells = GridCells.Adaptive(170.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = genreList) {
                    GenreCard(
                        genre = it,
                        modifier = Modifier.height(120.dp),
                        onClick = { onGenreItemClick(it) },
                        imageResourceId = getImageResourceForGenreType(it.genreType),
                        backgroundColor = getBackgroundColorForGenreType(it.genreType)
                    )
                }
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = isSearchListVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SearchQueryList(
                    searchResults = searchQueryResult,
                    onItemClick = { onSearchQueryItemClicked(it) },
                    onTrailingIconButtonClick = { /*TODO*/ },
                    isLoadingPlaceholderVisible = { item ->
                        isSearchItemLoadingPlaceholderVisibleMap.getOrPut(item) { false }
                    },
                    onImageLoadingFinished = { item, _ ->
                        isSearchItemLoadingPlaceholderVisibleMap[item] = false
                    },
                    onImageLoading = { isSearchItemLoadingPlaceholderVisibleMap[it] = true },
                    isSearchResultsLoadingAnimationVisible = isSearchResultLoading,
                    lottieComposition = searchResultsLoadingAnimationComposition,
                    lazyListState = lazyListState
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun SearchQueryList(
    searchResults: SearchResults,
    onItemClick: (SearchResult) -> Unit,
    onTrailingIconButtonClick: (SearchResult) -> Unit,
    isLoadingPlaceholderVisible: (SearchResult) -> Boolean,
    onImageLoading: (SearchResult) -> Unit,
    onImageLoadingFinished: (SearchResult, Throwable?) -> Unit,
    lottieComposition: LottieComposition?,
    lazyListState: LazyListState = rememberLazyListState(),
    isSearchResultsLoadingAnimationVisible: Boolean = false,
) {
    val artistImageErrorPainter =
        rememberVectorPainter(ImageVector.vectorResource(id = R.drawable.ic_outline_account_circle_24))
    val playlistImageErrorPainter =
        rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_outline_music_note_24))
    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = lazyListState,
        ) {
            items(searchResults.tracks, key = { it.id }) {
                MusifyCompactListItemCard(
                    cardType = it.getAssociatedListCardType(),
                    thumbnailImageUrlString = it.imageUrlString,
                    title = it.name,
                    subtitle = it.artistsString,
                    onClick = { onItemClick(it) },
                    onTrailingButtonIconClick = { onTrailingIconButtonClick(it) },
                    isLoadingPlaceHolderVisible = isLoadingPlaceholderVisible(it),
                    onThumbnailImageLoadingFinished = { throwable ->
                        onImageLoadingFinished(it, throwable)
                    },
                    onThumbnailLoading = { onImageLoading(it) }
                )
            }
            items(searchResults.albums, key = { it.id }) {
                MusifyCompactListItemCard(
                    cardType = it.getAssociatedListCardType(),
                    thumbnailImageUrlString = it.albumArtUrlString,
                    title = it.name,
                    subtitle = it.artistsString,
                    onClick = { onItemClick(it) },
                    onTrailingButtonIconClick = { onTrailingIconButtonClick(it) },
                    isLoadingPlaceHolderVisible = isLoadingPlaceholderVisible(it),
                    onThumbnailImageLoadingFinished = { throwable ->
                        onImageLoadingFinished(it, throwable)
                    },
                    onThumbnailLoading = { onImageLoading(it) }
                )
            }
            items(searchResults.artists, key = { it.id }) {
                MusifyCompactListItemCard(
                    cardType = it.getAssociatedListCardType(),
                    thumbnailImageUrlString = it.imageUrlString ?: "",
                    title = it.name,
                    subtitle = "Artist",
                    onClick = { onItemClick(it) },
                    onTrailingButtonIconClick = { onTrailingIconButtonClick(it) },
                    isLoadingPlaceHolderVisible = isLoadingPlaceholderVisible(it),
                    onThumbnailImageLoadingFinished = { throwable ->
                        onImageLoadingFinished(it, throwable)
                    },
                    onThumbnailLoading = { onImageLoading(it) },
                    errorPainter = artistImageErrorPainter
                )
            }
            itemsIndexed(searchResults.playlists) { index, it ->
                MusifyCompactListItemCard(
                    cardType = it.getAssociatedListCardType(),
                    thumbnailImageUrlString = it.imageUrlString ?: "",
                    title = it.name,
                    subtitle = "Playlist",
                    onClick = { onItemClick(it) },
                    onTrailingButtonIconClick = { onTrailingIconButtonClick(it) },
                    isLoadingPlaceHolderVisible = isLoadingPlaceholderVisible(it),
                    onThumbnailImageLoadingFinished = { throwable ->
                        onImageLoadingFinished(it, throwable)
                    },
                    onThumbnailLoading = { onImageLoading(it) },
                    errorPainter = playlistImageErrorPainter
                )
            }
            item {
                Spacer(modifier = Modifier.navigationBarsHeight())
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

private fun SearchResult.getAssociatedListCardType(): ListItemCardType = when (this) {
    is SearchResult.AlbumSearchResult -> ListItemCardType.ALBUM
    is SearchResult.ArtistSearchResult -> ListItemCardType.ARTIST
    is SearchResult.PlaylistSearchResult -> ListItemCardType.PLAYLIST
    is SearchResult.TrackSearchResult -> ListItemCardType.SONG
}

private fun getImageResourceForGenreType(genre: Genre.GenreType) = when (genre) {
    Genre.GenreType.AMBIENT -> R.drawable.genre_img_ambient
    Genre.GenreType.CHILL -> R.drawable.genre_img_chill
    Genre.GenreType.CLASSICAL -> R.drawable.genre_img_classical
    Genre.GenreType.DANCE -> R.drawable.genre_img_dance
    Genre.GenreType.ELECTRONIC -> R.drawable.genre_img_electronic
    Genre.GenreType.METAL -> R.drawable.genre_img_metal
    Genre.GenreType.RAINY_DAY -> R.drawable.genre_img_rainy_day
    Genre.GenreType.ROCK -> R.drawable.genre_img_rock
    Genre.GenreType.PIANO -> R.drawable.genre_img_piano
    Genre.GenreType.POP -> R.drawable.genre_img_pop
}

/**
 * Utility function used to get the associated background color of the
 * [genreType]. The were directly scraped from the official spotify
 * web app.
 * Note: Certain colors may not match with the official app. This
 * is because, all genres listed in the api where not listed in the
 * spotify web app.
 */
private fun getBackgroundColorForGenreType(genreType: Genre.GenreType) = when (genreType) {
    Genre.GenreType.AMBIENT -> Color(0, 48, 72)
    Genre.GenreType.CHILL -> Color(71, 126, 149)
    Genre.GenreType.CLASSICAL -> Color(141, 103, 171)
    Genre.GenreType.DANCE -> Color(140, 25, 50)
    Genre.GenreType.ELECTRONIC -> Color(186, 93, 7)
    Genre.GenreType.METAL -> Color(119, 119, 119)
    Genre.GenreType.RAINY_DAY -> Color(144, 168, 192)
    Genre.GenreType.ROCK -> Color(230, 30, 50)
    Genre.GenreType.PIANO -> Color(71, 125, 149)
    Genre.GenreType.POP -> Color(141, 103, 171)
}