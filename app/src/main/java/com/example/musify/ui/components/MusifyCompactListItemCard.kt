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
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.musify.R
import com.example.musify.utils.conditional
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
 * automatically set the trailing icon and the shape of the thumbnail
 * based on the [ListItemCardType], use the other overload. The composable
 * will ensure that it has a minimum height of 56.dp and a minimum width
 * of 250.dp. Size values below the minimum values will be ignored.
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
    modifier: Modifier = Modifier,
    thumbnailShape: Shape? = null,
    titleTextStyle: TextStyle = LocalTextStyle.current,
    subtitleTextStyle: TextStyle = LocalTextStyle.current,
    isLoadingPlaceHolderVisible: Boolean = false,
    onThumbnailLoading: (() -> Unit)? = null,
    onThumbnailLoadFailure: (() -> Unit)? = null,
    onThumbnailLoadSuccess: (() -> Unit)? = null,
    placeholderHighlight: PlaceholderHighlight = PlaceholderHighlight.shimmer(),
) {
    Card(
        modifier = Modifier
            .sizeIn(minHeight = 56.dp, minWidth = 250.dp)
            .then(modifier),
        elevation = 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f, true)
                    .weight(1f)
                    .conditional(thumbnailShape != null) { clip(thumbnailShape!!) }
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
                modifier = Modifier.weight(2f),
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
                modifier = Modifier.weight(1f),
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
 * on the [cardType]. The composable has predefined minimum size values.
 * Size values below the minimum values will be ignored. See the other
 * overload to know the minimum size values.
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
    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = LocalTextStyle.current,
    subtitleTextStyle: TextStyle = LocalTextStyle.current,
    isLoadingPlaceHolderVisible: Boolean = false,
    onThumbnailLoading: (() -> Unit)? = null,
    onThumbnailLoadFailure: (() -> Unit)? = null,
    onThumbnailLoadSuccess: (() -> Unit)? = null,
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
        onThumbnailLoadSuccess = onThumbnailLoadSuccess,
        onThumbnailLoadFailure = onThumbnailLoadFailure,
        placeholderHighlight = placeholderHighlight
    )
}