package com.example.musify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.musify.R
import com.example.musify.utils.conditional
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer

/**
 * An enum containing the all the types of list item cards.
 */
enum class ListItemCardType { ALBUM, ARTIST, SONG, PLAYLIST }

/**
 * A composable that represents a compact list item. This composable
 * contains an argument for setting the [trailingButtonIcon]. To
 * automatically set the trailing icon and the shape of the thumbnail
 * based on the [ListItemCardType], use the other overload. The composable
 * will ensure that it has a minimum height of 56.dp and a minimum width
 * of 250.dp. Size values below the minimum values will be ignored.
 * The maximum height of the composable can be of 80.dp. Any values
 * higher than 80.dp will be ignored, and the size would be set to 80.dp.
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
 * @param thumbnailShape the shape of the thumbnail image. If it is
 * not set, a square shape will be used.
 * @param titleTextStyle the style configuration for the [title] such as
 * color, font, line height etc.
 * @param subtitleTextStyle the style configuration for the [subtitle] such
 * as color, font, line height etc.
 * @param onThumbnailLoading the callback to execute when the thumbnail
 * image is loading.
 * @param onThumbnailImageLoadingFinished the lambda to execute when the image
 * is done loading. A nullable parameter of type [Throwable] is provided
 * to the lambda, that indicates whether the image loading process was
 * successful or not.
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
    modifier: Modifier = Modifier,
    thumbnailShape: Shape? = null,
    titleTextStyle: TextStyle = LocalTextStyle.current,
    subtitleTextStyle: TextStyle = LocalTextStyle.current,
    isLoadingPlaceHolderVisible: Boolean = false,
    onThumbnailLoading: (() -> Unit)? = null,
    onThumbnailImageLoadingFinished: ((Throwable?) -> Unit)? = null,
    placeholderHighlight: PlaceholderHighlight = PlaceholderHighlight.shimmer(),
) {
    Card(
        modifier = Modifier
            .sizeIn(minHeight = 56.dp, minWidth = 250.dp, maxHeight = 80.dp)
            .then(modifier),
        elevation = 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImageWithPlaceholder(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f, true)
                    .conditional(thumbnailShape != null) { clip(thumbnailShape!!) },
                model = thumbnailImageUrlString,
                contentScale = ContentScale.Crop,
                isLoadingPlaceholderVisible = isLoadingPlaceHolderVisible,
                onImageLoading = { onThumbnailLoading?.invoke() },
                onImageLoadingFinished = { onThumbnailImageLoadingFinished?.invoke(it) },
                placeholderHighlight = placeholderHighlight,
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
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
            IconButton(
                onClick = onTrailingButtonIconClick
            ) {
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
 * ensure the use of correct trailing icon and thumbnail shape based
 * on the [cardType]. The composable has predefined size values.
 * Size values below/above the minimum/maximum values will be ignored.
 * See the other overload to know the minimum/maximum size values.
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
 * @param onThumbnailImageLoadingFinished the lambda to execute when the image
 * is done loading. A nullable parameter of type [Throwable] is provided
 * to the lambda, that indicates whether the image loading process was
 * successful or not.
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
    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = LocalTextStyle.current,
    subtitleTextStyle: TextStyle = LocalTextStyle.current,
    isLoadingPlaceHolderVisible: Boolean = false,
    onThumbnailLoading: (() -> Unit)? = null,
    onThumbnailImageLoadingFinished: ((Throwable?) -> Unit)? = null,
    placeholderHighlight: PlaceholderHighlight = PlaceholderHighlight.shimmer()
) {
    MusifyCompactListItemCard(
        modifier = modifier,
        thumbnailImageUrlString = thumbnailImageUrlString,
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        trailingButtonIcon = when (cardType) {
            ListItemCardType.SONG -> Icons.Filled.MoreVert
            else -> ImageVector.vectorResource(id = R.drawable.ic_baseline_chevron_right_24)
        },
        onTrailingButtonIconClick = onTrailingButtonIconClick,
        thumbnailShape = if (cardType == ListItemCardType.ARTIST) CircleShape else null,
        titleTextStyle = titleTextStyle,
        subtitleTextStyle = subtitleTextStyle,
        isLoadingPlaceHolderVisible = isLoadingPlaceHolderVisible,
        onThumbnailLoading = onThumbnailLoading,
        onThumbnailImageLoadingFinished = onThumbnailImageLoadingFinished,
        placeholderHighlight = placeholderHighlight
    )
}