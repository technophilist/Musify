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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    searchQueryResult: SearchResults,
    onSearchQueryItemClicked: (SearchResult) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var isClearSearchTextButtonVisible by remember { mutableStateOf(false) }
    val isGenreImageLoadingMap = remember { mutableStateMapOf<String, Boolean>() }
    var isSearchListVisible by remember { mutableStateOf(false) }
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
    BackHandler(isSearchListVisible) {
        searchText = ""
        // remove focus on the search text field
        focusManager.clearFocus()
        isSearchListVisible = false
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
                        isClearSearchTextButtonVisible = true
                    } else {
                        isClearSearchTextButtonVisible = false
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
                    onImageLoading = { isSearchItemLoadingPlaceholderVisibleMap[it] = true }
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
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(searchResults.tracks) {
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
        items(searchResults.albums) {
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
        items(searchResults.artists) {
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
        items(searchResults.playlists) {
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
}

private fun SearchResult.getAssociatedListCardType(): ListItemCardType = when (this) {
    is SearchResult.AlbumSearchResult -> ListItemCardType.ALBUM
    is SearchResult.ArtistSearchResult -> ListItemCardType.ARTIST
    is SearchResult.PlaylistSearchResult -> ListItemCardType.PLAYLIST
    is SearchResult.TrackSearchResult -> ListItemCardType.SONG
}