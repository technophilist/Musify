package com.example.musify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.musify.R
import com.example.musify.domain.Streamable
import com.example.musify.ui.dynamicTheme.dynamicbackgroundmodifier.DynamicBackgroundResource
import com.example.musify.ui.dynamicTheme.dynamicbackgroundmodifier.DynamicBackgroundStyle
import com.example.musify.ui.dynamicTheme.dynamicbackgroundmodifier.dynamicBackground

/**
 * An object that contains constants related to the [MusifyMiniPlayer]
 * composable.
 */
object MusifyMiniPlayerConstants {
    val miniPlayerHeight = 60.dp
}

/**
 * A mini player composable.
 * It also displays 2 icons - Available Devices, and Play/Pause.
 *
 * Note: The size of this composable is **fixed to 60dp**.
 *
 * @param streamable the currently [Streamable].
 * @param isPlaybackPaused indicates whether the playback is paused.
 * Based on this, either [onPlayButtonClicked] or [onPauseButtonClicked]
 * will be invoked. Also, the play and pause icons will also be displayed
 * based on this parameter.
 * @param modifier the modifier to be applied to this composable.
 * @param onLikedButtonClicked the lambda to execute when the like
 * button is clicked. It is provided with a boolean that indicates
 * whether the the track is currently liked or not.
 * @param onPlayButtonClicked the lambda to execute when the play button
 * is clicked.
 * @param onPauseButtonClicked the lambda to execute when the pause button
 * is clicked.
 */
// TODO Make text scrollable if it overflows
// TODO debug recompositions
@Composable
fun MusifyMiniPlayer(
    streamable: Streamable,
    isPlaybackPaused: Boolean,
    modifier: Modifier = Modifier,
    onLikedButtonClicked: (Boolean) -> Unit, // todo remove param
    onPlayButtonClicked: () -> Unit,
    onPauseButtonClicked: () -> Unit
) {
    var isThumbnailImageLoading by remember { mutableStateOf(false) }
    val dynamicBackgroundResource = remember {
        DynamicBackgroundResource.FromImageUrl(streamable.streamInfo.imageUrl)
    }
    val dynamicBackgroundStyle = remember { DynamicBackgroundStyle.Filled() }
    Row(
        modifier = Modifier
            .fillMaxWidth() // todo remove, constant doesn't make sense for large screens
            .then(modifier)
            .heightIn(
                MusifyMiniPlayerConstants.miniPlayerHeight,
                MusifyMiniPlayerConstants.miniPlayerHeight
            ) // the height of this composable is fixed
            .clip(RoundedCornerShape(8.dp))
            .dynamicBackground(dynamicBackgroundResource, dynamicBackgroundStyle),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImageWithPlaceholder(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .aspectRatio(1f),
            model = streamable.streamInfo.imageUrl,
            contentDescription = null,
            onImageLoadingFinished = { isThumbnailImageLoading = false },
            isLoadingPlaceholderVisible = isThumbnailImageLoading,
            onImageLoading = { isThumbnailImageLoading = true },
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = streamable.streamInfo.title,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.subtitle2
            )
            Text(
                text = streamable.streamInfo.subtitle,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.caption.copy(
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(id = R.drawable.ic_available_devices),
                contentDescription = null
            )
        }
        IconButton(onClick = {
            if (isPlaybackPaused) {
                // if the playback is paused, then the play button
                // would be visible. Hence, invoke the lambda that
                // is required to be executed when the play button
                // is visible.
                onPlayButtonClicked()
            } else {
                // Similarly, if the track is being played, then the pause
                // button would be visible. Hence, invoke the lambda that
                // is required to be executed when the pause button
                // is visible.
                onPauseButtonClicked()
            }
        }) {
            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .aspectRatio(1f),
                painter = if (isPlaybackPaused) painterResource(R.drawable.ic_play_arrow_24)
                else painterResource(R.drawable.ic_pause_24),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}

