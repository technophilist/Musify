package com.example.musify.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
@Deprecated("Use other overload")
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

/**
 * A card that represents a Genre.
 *
 * @param genre the instance of [Genre] that the card is modeling.
 * @param imageResourceId a resource id that specifies the image to
 * be loaded as the thumbnail of the genre card.
 * @param modifier modifier to be applied to this composable.
 * @param onClick the lambda to execute when the card is clicked.
 * @param backgroundColor the background color.
 */
@ExperimentalMaterialApi
@Composable
fun GenreCard(
    genre: Genre,
    @DrawableRes imageResourceId: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colors.surface
) {
    Card(
        modifier = modifier,
        onClick = onClick ?: {},
        backgroundColor = backgroundColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                modifier = Modifier
                    .size(90.dp)
                    .offset(x = 110.dp, y = 30.dp)
                    .rotate(30f),
                painter = painterResource(id = imageResourceId),
                contentDescription = null
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