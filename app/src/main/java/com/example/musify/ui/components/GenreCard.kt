package com.example.musify.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.musify.domain.Genre
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

/**
 * A card that represents a Genre.
 *
 * @param genre the instance of [Genre] that the card is modeling.
 * @param modifier modifier to be applied to this composable.
 * @param isLoadingPlaceholderVisible indicates whether the loading
 * placeholder is visible.
 * @param onClick the lambda to execute when the card is clicked.
 * @param onImageLoading the callback to execute when the background
 * image is loading.
 * @param onImageLoadSuccess the callback to execute when the background
 * image was loaded successfully.
 * @param onImageLoadFailure the callback to execute if an error occurs
 * while loading the background image. The callback provides an instance
 * of [Throwable] that can be used to identify what caused the error.
 */
@ExperimentalMaterialApi
@Composable
fun GenreCard(
    genre: Genre,
    modifier: Modifier = Modifier,
    isLoadingPlaceholderVisible: Boolean = false,
    onClick: (() -> Unit)? = null,
    onImageLoading: (() -> Unit)? = null,
    onImageLoadSuccess: (() -> Unit)? = null,
    onImageLoadFailure: ((Throwable) -> Unit)? = null
) {
    Card(
        modifier = modifier,
        onClick = onClick ?: {}
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                modifier = Modifier.placeholder(
                    isLoadingPlaceholderVisible,
                    highlight = PlaceholderHighlight.shimmer()
                ),
                model = genre.coverArtUrl.toString(),
                contentScale = ContentScale.Crop,
                onState = {
                    when (it) {
                        is AsyncImagePainter.State.Empty -> {}
                        is AsyncImagePainter.State.Loading -> onImageLoading?.invoke()
                        is AsyncImagePainter.State.Success -> onImageLoadSuccess?.invoke()
                        is AsyncImagePainter.State.Error -> onImageLoadFailure?.invoke(it.result.throwable)
                    }
                },
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                text = genre.name,
                fontWeight = FontWeight.Bold
            )
        }
    }
}