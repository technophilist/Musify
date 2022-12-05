package com.example.musify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.musify.domain.SearchResult

@Suppress("RemoveSingleExpressionStringTemplate")
private fun SearchResult.EpisodeSearchResult.getDateAndDurationString(): String {
    return "${episodeReleaseDateInfo.month}" +
            " ${episodeReleaseDateInfo.day}," +
            " ${episodeReleaseDateInfo.year} • " +
            "${episodeDurationInfo.hours} hrs " +
            "${episodeDurationInfo.minutes} mins"
}

@ExperimentalMaterialApi
@Composable
fun EpisodeListCard(
    episodeSearchResult: SearchResult.EpisodeSearchResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
) {
    val dateAndDurationString = remember(episodeSearchResult) {
        episodeSearchResult.getDateAndDurationString()
    }
    var isLoadingPlaceholderVisible by remember { mutableStateOf(true) }
    Card(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .then(modifier),
        elevation = 0.dp,
        backgroundColor = backgroundColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImageWithPlaceholder(
                modifier = Modifier
                    .size(98.dp)
                    .clip(RoundedCornerShape(8.dp)),
                model = episodeSearchResult.episodeContentInfo.imageUrlString,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                onImageLoading = {
                    if (!isLoadingPlaceholderVisible) isLoadingPlaceholderVisible = true
                },
                onImageLoadingFinished = { isLoadingPlaceholderVisible = false },
                isLoadingPlaceholderVisible = isLoadingPlaceholderVisible
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = episodeSearchResult.episodeContentInfo.title,
                    style = MaterialTheme.typography.subtitle2,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = episodeSearchResult.episodeContentInfo.description,
                    style = MaterialTheme.typography.subtitle2.copy(
                        Color.White.copy(alpha = ContentAlpha.medium)
                    ),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = dateAndDurationString,
                    style = MaterialTheme.typography.subtitle2.copy(
                        Color.White.copy(alpha = ContentAlpha.medium)
                    ),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}