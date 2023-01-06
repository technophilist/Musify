package com.example.musify.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.example.musify.R
import com.example.musify.domain.PodcastShow
import com.example.musify.domain.SearchResult
import com.example.musify.domain.getFormattedDateAndDurationString
import com.example.musify.ui.components.AndroidExpandableTextView
import com.example.musify.ui.components.AsyncImageWithPlaceholder
import com.example.musify.ui.components.PodcastScreenEpisodeCard
import com.example.musify.ui.theme.dynamictheme.DynamicBackgroundType
import com.example.musify.ui.theme.dynamictheme.DynamicThemeResource
import com.example.musify.ui.theme.dynamictheme.DynamicallyThemedSurface

@ExperimentalMaterialApi
@Composable
fun PodcastShowDetailScreen(
    podcastShow: PodcastShow,
    onBackButtonClicked: () -> Unit,
    onEpisodePlayButtonClicked: (SearchResult.EpisodeSearchResult) -> Unit,
    onEpisodePauseButtonClicked: (SearchResult.EpisodeSearchResult) -> Unit,
    currentlyPlayingEpisode:SearchResult.EpisodeSearchResult,
    onEpisodeClicked: (SearchResult.EpisodeSearchResult) -> Unit,
    episodes: LazyPagingItems<SearchResult.EpisodeSearchResult>
) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        Header(
            imageUrlString = podcastShow.imageUrlString,
            onBackButtonClicked = onBackButtonClicked,
            title = podcastShow.name,
            nameOfPublisher = podcastShow.nameOfPublisher
        )
        // TODO Fix - text view doesn't expand
        AndroidExpandableTextView(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = podcastShow.htmlDescription,
            expandButtonText = "see more",
            textAppearanceResId = com.google.android.material.R.style.TextAppearance_MaterialComponents_Subtitle2,
            color = Color.White.copy(alpha = ContentAlpha.medium),
            maxLines = 2
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.White,
            text = "Episodes",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(modifier = Modifier.size(8.dp))
        LazyColumn {
            items(episodes) {
                it?.let { episode ->
                    PodcastScreenEpisodeCard(
                        isEpisodePlaying = currentlyPlayingEpisode == episode,
                        onPlayButtonClicked = { onEpisodePlayButtonClicked(episode) },
                        onPauseButtonClicked = { onEpisodePauseButtonClicked(episode) },
                        onClicked = { onEpisodeClicked(episode) },
                        title = episode.episodeContentInfo.title,
                        thumbnailImageUrlString = episode.episodeContentInfo.imageUrlString,
                        description = episode.episodeContentInfo.description,
                        dateAndDurationString = episode.getFormattedDateAndDurationString(context)
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(
    imageUrlString: String,
    onBackButtonClicked: () -> Unit,
    title: String,
    nameOfPublisher: String
) {
    val dynamicThemeResource = remember { DynamicThemeResource.FromImageUrl(imageUrlString) }
    val dynamicBackgroundType = remember { DynamicBackgroundType.Gradient() }
    val columnVerticalArrangementSpacing = 16.dp
    DynamicallyThemedSurface(
        dynamicThemeResource = dynamicThemeResource,
        dynamicBackgroundType = dynamicBackgroundType
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(columnVerticalArrangementSpacing)
        ) {
            IconButton(onClick = onBackButtonClicked) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_chevron_left_24),
                    contentDescription = null
                )
            }
            PodcastHeaderImageWithMetadata(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    // The back button above has a slightly bigger size because
                    // of the default touch target sizing applied by compose. This
                    // together with the vertical arrangement specified by the
                    // parent column, cause this composable have a lot of padding on
                    // top of it. Therefore, apply an offset to reduce the spacing
                    // above the composable.
                    .offset(y = -columnVerticalArrangementSpacing),
                imageUrl = imageUrlString,
                title = title,
                nameOfPublisher = nameOfPublisher
            )
            HeaderActionsRow(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .alpha(ContentAlpha.medium)
            )
        }
    }
}

@Composable
private fun PodcastHeaderImageWithMetadata(
    imageUrl: String,
    title: String,
    nameOfPublisher: String,
    modifier: Modifier = Modifier
) {
    var isThumbnailLoading by remember { mutableStateOf(true) }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImageWithPlaceholder(
            modifier = Modifier
                .size(176.dp)
                .clip(RoundedCornerShape(16.dp)),
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onImageLoadingFinished = { isThumbnailLoading = false },
            isLoadingPlaceholderVisible = isThumbnailLoading,
            onImageLoading = { isThumbnailLoading = true }
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(
                text = title,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = nameOfPublisher,
                maxLines = 1,
                style = MaterialTheme.typography.caption,
                color = Color.White
            )
        }
    }
}

@Composable
private fun HeaderActionsRow(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        OutlinedButton(
            onClick = { },
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = Color.Transparent,
                contentColor = Color.White,
            ),
            border = BorderStroke(1.dp, Color.White)
        ) {
            Text(
                text = "Follow",
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null
            )
        }
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null
            )
        }
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = null
            )
        }
    }
}