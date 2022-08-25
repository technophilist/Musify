package com.example.musify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.musify.domain.SearchResult
import com.example.musify.ui.components.DefaultMusifyLoadingAnimation
import com.example.musify.ui.components.ImageHeaderWithMetadata
import com.example.musify.ui.components.MusifyCompactTrackCard
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsPadding

@ExperimentalMaterialApi
@Composable
fun AlbumDetailScreen(
    albumName: String,
    artistsString: String,
    yearOfRelease: String,
    albumArtUrlString: String,
    trackList: List<SearchResult.TrackSearchResult>,
    onTrackItemClick: (SearchResult.TrackSearchResult) -> Unit,
    onBackButtonClicked: () -> Unit,
    isLoading: Boolean,
    isErrorMessageVisible: Boolean,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?
) {
    var isLoadingPlaceholderForAlbumArtVisible by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxSize()
        ) {
            item {
                ImageHeaderWithMetadata(
                    title = albumName,
                    imageUrl = albumArtUrlString,
                    subtitle = artistsString,
                    onBackButtonClicked = onBackButtonClicked,
                    isLoadingPlaceholderVisible = isLoadingPlaceholderForAlbumArtVisible,
                    onImageLoading = { isLoadingPlaceholderForAlbumArtVisible = true },
                    onImageLoaded = { isLoadingPlaceholderForAlbumArtVisible = false },
                    additionalMetadataContent = { AlbumArtHeaderMetadata(yearOfRelease) }
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
            if (isErrorMessageVisible) {
                item {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(),
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
                }
            } else {
                items(trackList) {
                    MusifyCompactTrackCard(
                        modifier = Modifier.padding(bottom = 16.dp),
                        track = it,
                        onClick = onTrackItemClick,
                        isLoadingPlaceholderVisible = false,
                        isCurrentlyPlaying = it == currentlyPlayingTrack,
                        isAlbumArtVisible = false,
                        subtitleTextStyle = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Thin,
                            color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.disabled),
                        )
                    )
                }
            }
            item {
                Spacer(
                    modifier = Modifier
                        .navigationBarsHeight()
                        .padding(bottom = 16.dp)
                )
            }
        }
        DefaultMusifyLoadingAnimation(
            modifier = Modifier.align(Alignment.Center),
            isVisible = isLoading
        )
    }
}

@Composable
private fun AlbumArtHeaderMetadata(yearOfRelease: String) {
    Text(
        text = "Album • $yearOfRelease",
        fontWeight = FontWeight.Normal,
        style = MaterialTheme.typography
            .subtitle2
            .copy(
                color = MaterialTheme.colors
                    .onBackground
                    .copy(alpha = ContentAlpha.medium)
            )
    )
}