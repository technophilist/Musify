package com.example.musify.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.musify.domain.Genre
import com.example.musify.domain.MusicSummary
import com.example.musify.ui.components.GenreCard
import com.example.musify.ui.components.ListItemCardType
import com.example.musify.ui.components.MusifyCompactListItemCard
import com.google.accompanist.insets.statusBarsPadding

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun SearchScreen(
    genreList: List<Genre>,
    onGenreItemClick: (Genre) -> Unit,
    onSearchTextChanged: (String) -> Unit,
    searchQueryResult: List<MusicSummary>,
    onSearchQueryItemClicked: (MusicSummary) -> Unit,
    onSearchQueryItemTrailingIconButtonClicked: (MusicSummary) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val isLoadingMap = remember { mutableStateMapOf<String, Boolean>() }
    var isSearchListVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
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
                    if (it.isFocused) isSearchListVisible = true
                },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            },
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
                        isLoadingPlaceholderVisible = isLoadingMap.getOrPut(it.id) { true },
                        onClick = { onGenreItemClick(it) },
                        onImageLoading = { isLoadingMap[it.id] = true },
                        onImageLoadingFinished = { _ -> isLoadingMap[it.id] = false }
                    )
                }
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = isSearchListVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SearchQueryList(
                    searchQueryResult = searchQueryResult,
                    onItemClick = { onSearchQueryItemClicked(it) },
                    onTrailingIconButtonClick = { onSearchQueryItemTrailingIconButtonClicked(it) }
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun SearchQueryList(
    searchQueryResult: List<MusicSummary>,
    onItemClick: (MusicSummary) -> Unit,
    onTrailingIconButtonClick: (MusicSummary) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(searchQueryResult) {
            MusifyCompactListItemCard(
                cardType = it.getAssociatedListCardType(),
                thumbnailImageUrlString = it.associatedImageUrl.toString(),
                title = it.name,
                subtitle = it.associatedMetadata ?: "",
                onClick = { onItemClick(it) },
                onTrailingButtonIconClick = { onTrailingIconButtonClick(it) }
            )
        }
    }
}

private fun MusicSummary.getAssociatedListCardType(): ListItemCardType = when (this) {
    is MusicSummary.AlbumSummary -> ListItemCardType.ALBUM
    is MusicSummary.ArtistSummary -> ListItemCardType.ARTIST
    is MusicSummary.PlaylistSummary -> ListItemCardType.PLAYLIST
    is MusicSummary.TrackSummary -> ListItemCardType.SONG
}