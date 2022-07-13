package com.example.musify.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalFocusManager
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
import com.example.musify.ui.components.GenreCard
import com.example.musify.ui.components.ListItemCardType
import com.example.musify.ui.components.MusifyCompactListItemCard
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsPadding

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun SearchScreen(
    genreList: List<Genre>,
    onGenreItemClick: (Genre) -> Unit,
    onSearchTextChanged: (String) -> Unit,
    isSearchResultLoading: Boolean,
    searchQueryResult: SearchResults,
    onSearchQueryItemClicked: (SearchResult) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val isGenreImageLoadingMap = remember { mutableStateMapOf<String, Boolean>() }
    var isSearchListVisible by remember { mutableStateOf(false) }
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

    BackHandler(isSearchListVisible) {
        // remove focus on the search text field
        focusManager.clearFocus()
        if (searchText.isEmpty()) isSearchListVisible = false
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Search",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h5
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
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
        Box {
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
                        isLoadingPlaceholderVisible = isGenreImageLoadingMap.getOrPut(it.id) { true },
                        onClick = { onGenreItemClick(it) },
                        onImageLoading = { isGenreImageLoadingMap[it.id] = true },
                        onImageLoadingFinished = { _ -> isGenreImageLoadingMap[it.id] = false }
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
                    lottieComposition = searchResultsLoadingAnimationComposition
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
    isSearchResultsLoadingAnimationVisible: Boolean = false,
    lottieComposition: LottieComposition?
) {
    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    onThumbnailLoading = { onImageLoading(it) }
                )
            }
            items(searchResults.playlists, key = { it.id }) {
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
                    onThumbnailLoading = { onImageLoading(it) }
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

private fun SearchResult.getAssociatedListCardType(): ListItemCardType = when (this) {
    is SearchResult.AlbumSearchResult -> ListItemCardType.ALBUM
    is SearchResult.ArtistSearchResult -> ListItemCardType.ARTIST
    is SearchResult.PlaylistSearchResult -> ListItemCardType.PLAYLIST
    is SearchResult.TrackSearchResult -> ListItemCardType.SONG
}