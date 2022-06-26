package com.example.musify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.musify.R
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

/**
 * An enum containing the all the types of list item cards.
 */
enum class ListItemCardType { ALBUM, ARTIST, SONG, PLAYLIST }

/**
 * A composable that represents a compact list item. This composable
 * contain an argument for setting the [trailingButtonIcon]. To
 * automatically set the trailing icon based on the [ListItemCardType]
 * use the other overload.
 *
 * @param thumbnailImageUrlString the url of the image to use as the
 * thumbnail.
 * @param title the title of the card.
 * @param subtitle the subtitle of the card.
 * @param onClick the callback to execute when the card is clicked.
 * @param trailingButtonIcon an instance of [ImageVector] that will be used
 * as the trailing icon.
 * @param onTrailingButtonIconClick the callback to execute when the
 * [trailingButtonIcon] is clicked.
 * @param titleTextStyle The style configuration for the [title] such as
 * color, font, line height etc.
 * @param subtitleTextStyle The style configuration for the [subtitle] such
 * as color, font, line height etc.
 * @param onThumbnailLoading the callback to execute when the thumbnail
 * image is loading.
 * @param onThumbnailLoadSuccess the callback to execute when the thumbnail
 * image was loaded successfully.
 * @param onThumbnailLoadFailure the callback to execute if an error occurs
 * while loading the thumbnail image.
 * @param isLoadingPlaceHolderVisible indicates whether the loading
 * placeholder is visible for the thumbnail image.
 */
@ExperimentalMaterialApi
@Composable
fun MusifyCompactListItemCard(
    thumbnailImageUrlString: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    trailingButtonIcon: ImageVector,
    onTrailingButtonIconClick: () -> Unit,
    titleTextStyle: TextStyle = LocalTextStyle.current,
    subtitleTextStyle: TextStyle = LocalTextStyle.current,
    isLoadingPlaceHolderVisible: Boolean = false,
    onThumbnailLoading: (() -> Unit)? = null,
    onThumbnailLoadFailure: (() -> Unit)? = null,
    onThumbnailLoadSuccess: (() -> Unit)? = null,
    placeholderHighlight: PlaceholderHighlight = PlaceholderHighlight.shimmer()
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp),
        elevation = 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(75.dp)
                    .placeholder(
                        visible = isLoadingPlaceHolderVisible,
                        highlight = placeholderHighlight
                    ),
                model = thumbnailImageUrlString,
                contentScale = ContentScale.Crop,
                onState = {
                    when (it) {
                        is AsyncImagePainter.State.Empty -> {}
                        is AsyncImagePainter.State.Loading -> onThumbnailLoading?.invoke()
                        is AsyncImagePainter.State.Success -> onThumbnailLoadSuccess?.invoke()
                        is AsyncImagePainter.State.Error -> onThumbnailLoadFailure?.invoke()
                    }
                },
                contentDescription = null
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = titleTextStyle
                )
                Text(
                    text = subtitle,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = subtitleTextStyle
                )
            }
            IconButton(onClick = onTrailingButtonIconClick) {
                Icon(
                    imageVector = trailingButtonIcon,
                    contentDescription = null
                )
            }
        }
    }
}

/**
 * A composable that represents a compact list item. This overload will
 * ensure the use of correct trailing icon based on the [cardType].
 * If a specific trailing icon is needed, use the other overload.
 *
 * @param thumbnailImageUrlString the url of the image to use as the
 * thumbnail.
 * @param title the title of the card.
 * @param subtitle the subtitle of the card.
 * @param onClick the callback to execute when the card is clicked
 * @param onTrailingButtonIconClick the callback to execute when the trailingButtonIcon
 * is clicked.
 * @param titleTextStyle The style configuration for the [title] such as
 * color, font, line height etc.
 * @param subtitleTextStyle The style configuration for the [subtitle] such
 * as color, font, line height etc.
 * @param onThumbnailLoading the callback to execute when the thumbnail
 * image is loading.
 * @param onThumbnailLoadSuccess the callback to execute when the thumbnail
 * image was loaded successfully.
 * @param onThumbnailLoadFailure the callback to execute if an error occurs
 * while loading the thumbnail image.
 * @param isLoadingPlaceHolderVisible indicates whether the loading
 * placeholder is visible for the thumbnail image.
 */
@ExperimentalMaterialApi
@Composable
fun MusifyCompactListItemCard(
    cardType: ListItemCardType,
    thumbnailImageUrlString: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    onTrailingButtonIconClick: () -> Unit,
    titleTextStyle: TextStyle = LocalTextStyle.current,
    subtitleTextStyle: TextStyle = LocalTextStyle.current,
    isLoadingPlaceHolderVisible: Boolean = false,
    onThumbnailLoading: (() -> Unit)? = null,
    onThumbnailLoadFailure: (() -> Unit)? = null,
    onThumbnailLoadSuccess: (() -> Unit)? = null,
    placeholderHighlight: PlaceholderHighlight = PlaceholderHighlight.shimmer()
) {
    MusifyCompactListItemCard(
        thumbnailImageUrlString = thumbnailImageUrlString,
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        trailingButtonIcon = when (cardType) {
            ListItemCardType.SONG -> Icons.Filled.MoreVert
            else -> ImageVector.vectorResource(id = R.drawable.ic_baseline_chevron_right_24)
        },
        onTrailingButtonIconClick = onTrailingButtonIconClick,
        titleTextStyle = titleTextStyle,
        subtitleTextStyle = subtitleTextStyle,
        isLoadingPlaceHolderVisible = isLoadingPlaceHolderVisible,
        onThumbnailLoading = onThumbnailLoading,
        onThumbnailLoadSuccess = onThumbnailLoadSuccess,
        onThumbnailLoadFailure = onThumbnailLoadFailure,
        placeholderHighlight = placeholderHighlight
    )
}