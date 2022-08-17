package com.example.musify.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch

// TODO remove temporarily used item{} for artist image header
@ExperimentalMaterialApi
@Composable
fun ArtistDetailScreen(
    artistName: String,
    artistImageUrlString: String,
    popularTracks: List<SearchResult.TrackSearchResult>,
    releases: LazyPagingItems<SearchResult.AlbumSearchResult>,
    currentlyPlayingTrack:SearchResult.TrackSearchResult?,
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
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val trackPlayingTextStyle = LocalTextStyle.current.copy(
        color = MaterialTheme.colors.primary
    )
    Box {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = lazyListState
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
            itemsIndexed(popularTracks) { index, track ->
                Row(
                    modifier = Modifier.height(64.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = if (index + 1 < 10) Modifier.padding(16.dp)
                        else Modifier.padding(
                            top = 16.dp,
                            bottom = 16.dp,
                            start = 16.dp,
                            end = 8.dp
                        ),
                        text = "${index + 1}"
                    )
                    MusifyCompactListItemCard(
                        cardType = ListItemCardType.TRACK,
                        thumbnailImageUrlString = track.imageUrlString,
                        title = track.name,
                        subtitle = track.artistsString,
                        subtitleTextStyle = MaterialTheme.typography
                            .caption
                            .copy(color = subtitleTextColorWithAlpha),
                        onClick = { onTrackClicked(track) },
                        onTrailingButtonIconClick = { /*TODO*/ },
                        titleTextStyle = if (track == currentlyPlayingTrack)
                            trackPlayingTextStyle else LocalTextStyle.current,
                    )
                }
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
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(bottom = 16.dp, end = 16.dp),
            visible = lazyListState.firstVisibleItemIndex > 10,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FloatingActionButton(
                backgroundColor = MaterialTheme.colors.primary,
                onClick = { coroutineScope.launch { lazyListState.animateScrollToItem(0) } },
            ) { Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = null) }
        }
    }
}

@Composable
private fun SubtitleText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.h5,
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