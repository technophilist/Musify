package com.example.musify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.musify.R
import com.example.musify.domain.SearchResult
import com.example.musify.ui.components.AsyncImageWithPlaceholder
import com.example.musify.ui.components.DefaultMusifyLoadingAnimation
import com.example.musify.ui.components.MusifyCompactTrackCard
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsPadding

/**
 * An enum representing the different types of MusicDetailScreens.
 * Based on this, certain display properties may be added/removed
 * in the composable.
 */
enum class MusicDetailScreenType { ALBUM, PLAYLIST }

@ExperimentalMaterialApi
@Composable
fun MusicDetailScreen(
    artUrl: String,
    musicDetailScreenType: MusicDetailScreenType,
    title: String,
    nameOfUploader: String,
    metadata: String,
    trackList: List<SearchResult.TrackSearchResult>,
    onTrackItemClick: (SearchResult.TrackSearchResult) -> Unit,
    onBackButtonClicked: () -> Unit,
    isLoading: Boolean,
    isErrorMessageVisible: Boolean,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?
) {
    val metadataText = "${
        when (musicDetailScreenType) {
            MusicDetailScreenType.ALBUM -> "Album"
            MusicDetailScreenType.PLAYLIST -> "Playlist"
        }
    } • $metadata"
    var isLoadingPlaceholderForAlbumArtVisible by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxSize()
        ) {
            item {
                ArtWithHeader(
                    artUrl = artUrl,
                    title = title,
                    nameOfUploader = nameOfUploader,
                    metadata = metadataText,
                    isLoadingPlaceholderVisible = isLoadingPlaceholderForAlbumArtVisible,
                    onAlbumArtLoading = { isLoadingPlaceholderForAlbumArtVisible = true },
                    onAlbumArtLoaded = { isLoadingPlaceholderForAlbumArtVisible = false },
                    onBackButtonClicked = onBackButtonClicked
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
private fun ArtWithHeader(
    artUrl: String,
    title: String,
    nameOfUploader: String,
    metadata: String,
    isLoadingPlaceholderVisible: Boolean = false,
    onAlbumArtLoading: () -> Unit,
    onAlbumArtLoaded: (Throwable?) -> Unit,
    onBackButtonClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .clip(CircleShape)
                .background(MaterialTheme.colors.background.copy(alpha = 0.5f))
                .padding(8.dp),
            onClick = onBackButtonClicked
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_chevron_left_24),
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImageWithPlaceholder(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
                    .shadow(8.dp),
                model = artUrl,
                contentDescription = null,
                isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
                onImageLoading = onAlbumArtLoading,
                onImageLoadingFinished = onAlbumArtLoaded,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h5
            )
            Text(
                text = nameOfUploader,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                text = metadata,
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
    }
}