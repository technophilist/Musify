package com.example.musify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import com.example.musify.domain.MusicSummary
import com.example.musify.ui.components.AsyncImageWithPlaceholder
import com.example.musify.ui.components.MusifyCompactListItemCard
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
    trackList: List<MusicSummary.TrackSummary>,
    onTrackItemClick: (MusicSummary.TrackSummary) -> Unit,
    onTrackTrailingButtonClick: (MusicSummary.TrackSummary) -> Unit,
    onBackButtonClicked: () -> Unit
) {
    val metadataText = "${
        when (musicDetailScreenType) {
            MusicDetailScreenType.ALBUM -> "Album"
            MusicDetailScreenType.PLAYLIST -> "Playlist"
        }
    } • $metadata"
    var isLoadingPlaceholderVisible by remember { mutableStateOf(false) }
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
                isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
                onAlbumArtLoading = { isLoadingPlaceholderVisible = true },
                onAlbumArtLoaded = { isLoadingPlaceholderVisible = false },
                onBackButtonClicked = onBackButtonClicked
            )
        }
        items(trackList) {
            MusifyCompactListItemCard(
                title = it.name,
                subtitle = it.albumName,
                onClick = { onTrackItemClick(it) },
                trailingButtonIcon = Icons.Filled.MoreVert,
                onTrailingButtonIconClick = { onTrackTrailingButtonClick(it) },
                subtitleTextStyle = MaterialTheme.typography
                    .caption
                    .copy(
                        color = MaterialTheme.colors
                            .onBackground
                            .copy(ContentAlpha.disabled)
                    )
            )
        }
        item {
            Spacer(
                modifier = Modifier
                    .navigationBarsHeight()
                    .padding(bottom = 16.dp)
            )
        }
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
                .padding(16.dp),
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