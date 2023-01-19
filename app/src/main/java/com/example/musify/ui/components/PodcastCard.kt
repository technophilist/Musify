package com.example.musify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * A card that shows basic information about a particular podcast.
 * Note: The width of the composable is fixed at 175dp.
 *
 * @param podcastArtUrlString the url of the podcast art.
 * @param name the name of the podcast.
 * @param nameOfPublisher the name of the publisher of the podcast.
 * @param onClick the lambda to execute when the item is clicked.
 * @param modifier the modifier to be applied to the composable. The
 * width is fixed at 160dp.
 */
@ExperimentalMaterialApi
@Composable
fun PodcastCard(
    podcastArtUrlString: String,
    name: String,
    nameOfPublisher: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoadingPlaceholderVisible by remember { mutableStateOf(true) }
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(IntrinsicSize.Min)
            .then(modifier),
        backgroundColor = Color.Transparent,
        onClick = onClick,
        shape = RectangleShape,
        elevation = 0.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AsyncImageWithPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp)),
                model = podcastArtUrlString,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
                onImageLoading = { isLoadingPlaceholderVisible = true },
                onImageLoadingFinished = { isLoadingPlaceholderVisible = false }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = nameOfPublisher,
                style = MaterialTheme.typography.subtitle2.copy(
                    Color.White.copy(alpha = ContentAlpha.medium)
                ),
                fontWeight = FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}