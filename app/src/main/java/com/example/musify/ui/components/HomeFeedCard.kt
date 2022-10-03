package com.example.musify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * A card meant to be used in the home feed.
 * Note: The width of the composable is fixed at 170dp.
 *
 * @param imageUrlString the url of the image.
 * @param caption the text to be displayed at the bottom of the image.
 * @param onClick the lambda to execute when the item is clicked
 * @param modifier the modifier to be applied to the composable. The
 * width is fixed at 170dp.
 */
@ExperimentalMaterialApi
@Composable
fun HomeFeedCard(
    imageUrlString: String,
    caption: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .widthIn(min = 160.dp, max = 160.dp)
            .height(IntrinsicSize.Min)
            .then(modifier),
        backgroundColor = Color.Transparent,
        onClick = onClick,
        shape = RectangleShape,
        elevation = 0.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                model = imageUrlString,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.alpha(ContentAlpha.medium),
                text = caption,
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}