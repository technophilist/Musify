package com.example.musify.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.musify.domain.Genre
import com.example.musify.ui.components.GenreCard
import com.google.accompanist.insets.statusBarsPadding

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun SearchScreen(
    genreList: List<Genre>,
    onGenreItemClick: (Genre) -> Unit,
    onSearchTextChanged: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val isLoadingMap = remember { mutableStateMapOf<String, Boolean>() }
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
            modifier = Modifier.fillMaxWidth(),
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
                    onImageLoadFailure = { _ -> isLoadingMap[it.id] = false },
                    onImageLoadSuccess = { isLoadingMap[it.id] = false }
                )
            }
        }
    }
}
