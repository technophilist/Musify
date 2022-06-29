package com.example.musify.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.AsyncImagePainter.State
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

/**
 * An [AsyncImage] composable overload that contains parameters for
 * placeholder.
 *
 * @param model Either an [ImageRequest] or the [ImageRequest.data] value.
 * @param contentDescription Text used by accessibility services to
 * describe what this image represents. This should always be provided
 * unless this image is used for decorative purposes, and does not
 * represent a meaningful action that a user can take.
 * @param onImageLoading the lambda to execute when the image is loading.
 * @param onImageLoadingFinished the lambda to execute when the image
 * is done loading. A nullable parameter of type [Throwable] is provided
 * to the lambda, that indicates whether the image loading process was
 * successful or not.
 * @param modifier Modifier used to adjust the layout algorithm or
 * draw decoration content.
 * @param isLoadingPlaceholderVisible indicates whether the placeholder
 * is visible.
 * @param placeholderHighlight highlight optional highlight animation
 * for the placeholder. The default animation is [PlaceholderHighlight.shimmer()].
 * @param transform A callback to transform a new [State] before
 * it's applied to the [AsyncImagePainter]. Typically this is used
 * to modify the state's [Painter].
 * @param alignment Optional alignment parameter used to place the
 * [AsyncImagePainter] in the given bounds defined by the width
 * and height.
 * @param contentScale Optional scale parameter used to determine
 * the aspect ratio scaling to be used if the bounds are a different
 * size from the intrinsic size of the [AsyncImagePainter].
 * @param alpha Optional opacity to be applied to the
 * [AsyncImagePainter] when it is rendered onscreen.
 * @param colorFilter Optional [ColorFilter] to apply for the
 * [AsyncImagePainter] when it is rendered onscreen.
 * @param filterQuality Sampling algorithm applied to a bitmap when
 * it is scaled and drawn into the destination.
 */
@Composable
fun AsyncImageWithPlaceholder(
    model: Any?,
    contentDescription: String?,
    onImageLoadingFinished: (Throwable?) -> Unit,
    onImageLoading: () -> Unit,
    modifier: Modifier = Modifier,
    isLoadingPlaceholderVisible: Boolean = false,
    placeholderHighlight: PlaceholderHighlight = PlaceholderHighlight.shimmer(),
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
) {
    AsyncImage(
        modifier = modifier.placeholder(
            visible = isLoadingPlaceholderVisible,
            highlight = placeholderHighlight
        ),
        model = model,
        contentDescription = contentDescription,
        transform = transform,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        onState = {
            when (it) {
                AsyncImagePainter.State.Empty -> {}
                is AsyncImagePainter.State.Loading -> onImageLoading()
                is AsyncImagePainter.State.Success -> onImageLoadingFinished(null)
                is AsyncImagePainter.State.Error -> onImageLoadingFinished(it.result.throwable)
            }
        }
    )
}