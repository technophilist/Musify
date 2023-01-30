package com.example.musify.ui.screens.detailscreens

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.example.musify.domain.SearchResult
import com.example.musify.ui.components.*
import com.example.musify.ui.theme.dynamictheme.DynamicBackgroundType
import com.example.musify.ui.theme.dynamictheme.DynamicThemeResource
import com.example.musify.ui.theme.dynamictheme.DynamicallyThemedSurface
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@Composable
fun PlaylistDetailScreen(
    playlistName: String,
    playlistImageUrlString: String?,
    nameOfPlaylistOwner: String,
    totalNumberOfTracks: String,
    @DrawableRes imageResToUseWhenImageUrlStringIsNull: Int,
    tracks: LazyPagingItems<SearchResult.TrackSearchResult>,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    onBackButtonClicked: () -> Unit,
    onTrackClicked: (SearchResult.TrackSearchResult) -> Unit,
    isLoading: Boolean,
    isErrorMessageVisible: Boolean
) {
    var isLoadingPlaceholderForAlbumArtVisible by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val isAppBarVisible by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex > 0 }
    }
    val dynamicThemeResource = remember {
        if (playlistImageUrlString == null) DynamicThemeResource.Empty
        else DynamicThemeResource.FromImageUrl(playlistImageUrlString)

    }
    val dynamicBackgroundType = remember { DynamicBackgroundType.Gradient() }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = MusifyBottomNavigationConstants.navigationHeight + MusifyMiniPlayerConstants.miniPlayerHeight
            ),
            state = lazyListState
        ) {
            headerWithImageItem(
                dynamicThemeResource = dynamicThemeResource,
                dynamicBackgroundType = dynamicBackgroundType,
                playlistName = playlistName,
                playlistImageUrlString = playlistImageUrlString,
                imageResToUseWhenImageUrlStringIsNull = imageResToUseWhenImageUrlStringIsNull,
                nameOfPlaylistOwner = nameOfPlaylistOwner,
                totalNumberOfTracks = totalNumberOfTracks,
                isLoadingPlaceholderForAlbumArtVisible = isLoadingPlaceholderForAlbumArtVisible,
                onImageLoading = { isLoadingPlaceholderForAlbumArtVisible = true },
                onImageLoaded = { isLoadingPlaceholderForAlbumArtVisible = false },
                onBackButtonClicked = onBackButtonClicked
            )
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
                            isAlbumArtVisible = true,
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
                        .windowInsetsBottomHeight(WindowInsets.navigationBars)
                        .padding(bottom = 16.dp)
                )
            }
        }
        DefaultMusifyLoadingAnimation(
            modifier = Modifier.align(Alignment.Center),
            isVisible = isLoading
        )
        AnimatedVisibility(
            visible = isAppBarVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            DetailScreenTopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding(),
                title = playlistName,
                onBackButtonClicked = onBackButtonClicked,
                dynamicThemeResource = dynamicThemeResource,
                onClick = {
                    coroutineScope.launch { lazyListState.animateScrollToItem(0) }
                }
            )
        }
    }
}

private fun LazyListScope.headerWithImageItem(
    dynamicThemeResource: DynamicThemeResource,
    dynamicBackgroundType: DynamicBackgroundType,
    playlistName: String,
    playlistImageUrlString: String?,
    @DrawableRes imageResToUseWhenImageUrlStringIsNull: Int,
    nameOfPlaylistOwner: String,
    totalNumberOfTracks: String,
    isLoadingPlaceholderForAlbumArtVisible: Boolean,
    onImageLoading: () -> Unit,
    onImageLoaded: (Throwable?) -> Unit,
    onBackButtonClicked: () -> Unit
) {
    item {
        DynamicallyThemedSurface(
            dynamicThemeResource = dynamicThemeResource,
            dynamicBackgroundType = dynamicBackgroundType
        ) {
            Column(modifier = Modifier.statusBarsPadding()) {
                ImageHeaderWithMetadata(
                    title = playlistName,
                    headerImageSource = if (playlistImageUrlString == null)
                        HeaderImageSource.ImageFromDrawableResource(
                            resourceId = imageResToUseWhenImageUrlStringIsNull
                        )
                    else HeaderImageSource.ImageFromUrlString(playlistImageUrlString),
                    subtitle = "by $nameOfPlaylistOwner • $totalNumberOfTracks tracks",
                    onBackButtonClicked = onBackButtonClicked,
                    isLoadingPlaceholderVisible = isLoadingPlaceholderForAlbumArtVisible,
                    onImageLoading = onImageLoading,
                    onImageLoaded = onImageLoaded,
                    additionalMetadataContent = { }
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}