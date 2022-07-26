package com.example.musify.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.musify.domain.Genre

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
 * @param onImageLoadingFinished the lambda to execute when the image
 * is done loading. A nullable parameter of type [Throwable] is provided
 * to the lambda, that indicates whether the image loading process was
 * successful or not.
 * of [Throwable] that can be used to identify what caused the error.
 */
@ExperimentalMaterialApi
@Composable
fun GenreCard(
    genre: Genre,
    modifier: Modifier = Modifier,
    isLoadingPlaceholderVisible: Boolean = false,
    onClick: (() -> Unit)? = null,
    onImageLoading: () -> Unit,
    onImageLoadingFinished: (Throwable?) -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick ?: {}
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImageWithPlaceholder(
                model = "", // TODO
                contentScale = ContentScale.Crop,
                contentDescription = null,
                isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
                onImageLoading = onImageLoading,
                onImageLoadingFinished = onImageLoadingFinished
            )
            Text(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                text = genre.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6
            )
        }
    }
}