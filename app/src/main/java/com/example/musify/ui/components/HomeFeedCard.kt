package com.example.musify.ui.components

import android.graphics.Typeface
import android.text.TextUtils
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
    val mediumContentAlpha = ContentAlpha.medium
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
            // Min lines not yet supported in compose
            // switch this out with Text composable once it
            // becomes available
            // https://issuetracker.google.com/issues/122476634
            AndroidView(factory = {
                TextView(it).apply {
                    maxLines = 2
                    // the min lines must be the same as max lines.
                    // This is because, a card may contain varying
                    // number of characters. This may cause a card
                    // with 2 lines of text to have a larger height
                    // than the cards which have a single line of text.
                    // This is an issue because, if this composable
                    // was displayed in a list, it will cause the
                    // the size of the list to change based on the
                    // item displayed, which in-turn would make the
                    // ui behave "janky". This becomes a problem
                    // especially when this composable is used
                    // inside a lazy composable.
                    minLines = maxLines
                    alpha = mediumContentAlpha
                    text = caption
                    ellipsize = TextUtils.TruncateAt.END
                    setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Caption)
                    setTypeface(typeface, Typeface.NORMAL)
                }
            })
        }
    }
}