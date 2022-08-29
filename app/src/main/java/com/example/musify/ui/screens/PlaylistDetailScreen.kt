package com.example.musify.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.example.musify.domain.SearchResult
import com.example.musify.ui.components.DefaultMusifyLoadingAnimation
import com.example.musify.ui.components.HeaderImageSource
import com.example.musify.ui.components.ImageHeaderWithMetadata
import com.example.musify.ui.components.MusifyCompactTrackCard
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsPadding


@ExperimentalMaterialApi
@Composable
fun PlaylistDetailScreen(
    playlistName: String,
    playlistImageUrlString: String?,
    @DrawableRes imageResToUseWhenImageUrlStringIsNull: Int,
    tracks: LazyPagingItems<SearchResult.TrackSearchResult>,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    onBackButtonClicked: () -> Unit,
    onTrackClicked: (SearchResult.TrackSearchResult) -> Unit,
    isLoading: Boolean,
    isErrorMessageVisible: Boolean
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
                    title = playlistName,
                    headerImageSource = if (playlistImageUrlString == null)
                        HeaderImageSource.ImageFromDrawableResource(
                            resourceId = imageResToUseWhenImageUrlStringIsNull
                        )
                    else HeaderImageSource.ImageFromUrlString(playlistImageUrlString),
                    subtitle = "TODO",
                    onBackButtonClicked = onBackButtonClicked,
                    isLoadingPlaceholderVisible = isLoadingPlaceholderForAlbumArtVisible,
                    onImageLoading = { isLoadingPlaceholderForAlbumArtVisible = true },
                    onImageLoaded = { isLoadingPlaceholderForAlbumArtVisible = false },
                    additionalMetadataContent = { /*TODO*/ }
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
                items(tracks) {
                    it?.let {
                        MusifyCompactTrackCard(
                            track = it,
                            onClick = onTrackClicked,
                            isLoadingPlaceholderVisible = false,
                            isCurrentlyPlaying = it == currentlyPlayingTrack,
                            isAlbumArtVisible = false,
                            subtitleTextStyle = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.Thin,
                                color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.disabled),
                            ),
                            contentPadding = PaddingValues(16.dp)
                        )
                    }
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