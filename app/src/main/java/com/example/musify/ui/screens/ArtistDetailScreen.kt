package com.example.musify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.musify.domain.MusicSummary
import com.example.musify.ui.components.AsyncImageWithPlaceholder
import com.example.musify.ui.components.ListItemCardType
import com.example.musify.ui.components.MusifyCompactListItemCard
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsPadding

@ExperimentalMaterialApi
@Composable
fun ArtistDetailScreen(
    artistSummary: MusicSummary.ArtistSummary,
    popularTracks: List<MusicSummary.TrackSummary>,
    popularReleases: List<MusicSummary.AlbumSummary>,
    onBackButtonClicked: () -> Unit,
    onPLayButtonClicked: () -> Unit,
    onTrackClicked: (MusicSummary.TrackSummary) -> Unit,
    onTrackTrailingButtonIconClicked: (MusicSummary.TrackSummary) -> Unit,
    onAlbumClicked: (MusicSummary.AlbumSummary) -> Unit,
) {
    val subtitleTextColorWithAlpha = MaterialTheme.colors.onBackground.copy(
        alpha = ContentAlpha.disabled
    )
    var isCoverArtPlaceholderVisible by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        artistCoverArtHeaderItem(
            artistName = artistSummary.name,
            artistCoverArtUrlString = artistSummary.associatedImageUrl.toString(),
            onBackButtonClicked = onBackButtonClicked,
            onPLayButtonClick = onPLayButtonClicked,
            isLoadingPlaceholderVisible = isCoverArtPlaceholderVisible,
            onCoverArtLoading = { isCoverArtPlaceholderVisible = true },
            onCoverArtLoaded = { isCoverArtPlaceholderVisible = false }
        )
        item {
            SubtitleText(
                modifier = Modifier.padding(start = 16.dp),
                text = "Popular tracks"
            )
        }
        items(popularTracks) {
            MusifyCompactListItemCard(
                modifier = Modifier
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                cardType = ListItemCardType.SONG,
                thumbnailImageUrlString = it.associatedImageUrl.toString(),
                title = it.name,
                subtitle = it.albumName,
                subtitleTextStyle = MaterialTheme.typography
                    .caption
                    .copy(color = subtitleTextColorWithAlpha),
                onClick = { onTrackClicked(it) },
                onTrailingButtonIconClick = { onTrackTrailingButtonIconClicked(it) }
            )
        }
        item {
            SubtitleText(
                modifier = Modifier.padding(start = 16.dp),
                text = "Popular Releases"
            )
        }
        items(popularReleases) {
            MusifyCompactListItemCard(
                modifier = Modifier
                    .height(80.dp)
                    .padding(horizontal = 16.dp),
                cardType = ListItemCardType.ALBUM,
                thumbnailImageUrlString = it.albumArtUrl.toString(),
                title = it.nameOfArtist,
                titleTextStyle = MaterialTheme.typography.h6,
                subtitle = it.yearOfReleaseString,
                subtitleTextStyle = MaterialTheme.typography
                    .subtitle2
                    .copy(color = subtitleTextColorWithAlpha),
                onClick = { onAlbumClicked(it) },
                onTrailingButtonIconClick = { onAlbumClicked(it) }
            )
        }
        item {
            Spacer(modifier = Modifier.navigationBarsHeight())
        }
    }

}

@Composable
private fun SubtitleText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.subtitle1,
        fontWeight = FontWeight.Bold
    )
}

private fun LazyListScope.artistCoverArtHeaderItem(
    artistName: String,
    artistCoverArtUrlString: String,
    onBackButtonClicked: () -> Unit,
    onPLayButtonClick: () -> Unit,
    isLoadingPlaceholderVisible: Boolean = false,
    onCoverArtLoading: (() -> Unit)? = null,
    onCoverArtLoaded: ((Throwable?) -> Unit)? = null,
) {
    item {
        Box(
            modifier = Modifier
                .fillParentMaxHeight(0.6f)
                .fillParentMaxWidth()
        ) {
            AsyncImageWithPlaceholder(
                modifier = Modifier.fillMaxSize(),
                model = artistCoverArtUrlString,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
                onImageLoading = { onCoverArtLoading?.invoke() },
                onImageLoadingFinished = { onCoverArtLoaded?.invoke(it) }
            )
            // scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(16.dp)
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.background.copy(alpha = 0.5f)),
                onClick = onBackButtonClicked
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                text = artistName,
                style = MaterialTheme.typography.h3,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp)
                    .offset(y = 24.dp),
                backgroundColor = MaterialTheme.colors.primary,
                onClick = onPLayButtonClick
            ) {
                Icon(
                    modifier = Modifier.size(50.dp),
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null
                )
            }
        }
    }
}