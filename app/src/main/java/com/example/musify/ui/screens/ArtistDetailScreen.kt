package com.example.musify.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.example.musify.domain.SearchResult
import com.example.musify.ui.components.AsyncImageWithPlaceholder
import com.example.musify.ui.components.ListItemCardType
import com.example.musify.ui.components.MusifyCompactListItemCard
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsPadding

@ExperimentalMaterialApi
@Composable
fun ArtistDetailScreen(
    artistName: String,
    artistImageUrlString: String,
    popularTracks: List<SearchResult.TrackSearchResult>,
    releases: LazyPagingItems<SearchResult.AlbumSearchResult>,
    onBackButtonClicked: () -> Unit,
    onPlayButtonClicked: () -> Unit,
    onTrackClicked: (SearchResult.TrackSearchResult) -> Unit,
    onAlbumClicked: (SearchResult.AlbumSearchResult) -> Unit,
    isLoading: Boolean,
    loadingAnimationComposition: LottieComposition?,
) {
    val subtitleTextColorWithAlpha = MaterialTheme.colors.onBackground.copy(
        alpha = ContentAlpha.disabled
    )
    var isCoverArtPlaceholderVisible by remember { mutableStateOf(false) }
    Box {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            artistCoverArtHeaderItem(
                artistName = artistName,
                artistCoverArtUrlString = artistImageUrlString, // TODO
                onBackButtonClicked = onBackButtonClicked,
                onPLayButtonClick = onPlayButtonClicked,
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
                    cardType = ListItemCardType.TRACK,
                    thumbnailImageUrlString = it.imageUrlString,
                    title = it.name,
                    subtitle = it.artistsString,
                    subtitleTextStyle = MaterialTheme.typography
                        .caption
                        .copy(color = subtitleTextColorWithAlpha),
                    onClick = { onTrackClicked(it) },
                    onTrailingButtonIconClick = { /*TODO*/ }
                )
            }
            item {
                SubtitleText(
                    modifier = Modifier.padding(start = 16.dp),
                    text = "Releases"
                )
            }
            itemsIndexed(
                items = releases,
                key = { index, album -> "$index$album" }
            ) { _, album ->
                album?.let {
                    MusifyCompactListItemCard(
                        modifier = Modifier
                            .height(80.dp)
                            .padding(horizontal = 16.dp),
                        cardType = ListItemCardType.ALBUM,
                        thumbnailImageUrlString = it.albumArtUrlString,
                        title = it.name,
                        titleTextStyle = MaterialTheme.typography.h6,
                        subtitle = it.yearOfReleaseString,
                        subtitleTextStyle = MaterialTheme.typography
                            .subtitle2
                            .copy(color = subtitleTextColorWithAlpha),
                        onClick = { onAlbumClicked(it) },
                        onTrailingButtonIconClick = { onAlbumClicked(it) }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.navigationBarsHeight())
            }
        }
        AnimatedVisibility(
            modifier = Modifier
                .size(128.dp) // the actual size will be mush lesser because of padding and offset
                .align(Alignment.Center)
                .clip(RoundedCornerShape(5))
                .background(Color.Black.copy(alpha = 0.3f)),
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LottieAnimation(
                composition = loadingAnimationComposition,
                iterations = LottieConstants.IterateForever
            )
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