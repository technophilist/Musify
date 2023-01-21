package com.example.musify.ui.components

import android.text.Spanned
import android.text.TextUtils
import android.text.util.Linkify
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.textview.MaterialTextView

/**
 * A text composable that supports the use of [Spanned] text.
 * Since [Spanned] text is not yet supported in compose, this composable
 * internally uses [AndroidView] to inflate a [MaterialTextView].
 *
 * @param text the text to be displayed.
 * @param modifier the modifier to be applied to the text composable.
 * @param textAppearanceResId Sets the text appearance for the [text] based on the
 * specified style resource. For example, one might use
 * [com.google.android.material.R.style.TextAppearance_MaterialComponents_Subtitle2]
 * as the text appearance.
 * @param color the [Color] to apply to the text. If [Color.Unspecified], and style has no color set,
 * this will be LocalContentColor.
 * @param maxLines  An optional maximum number of lines for the text to span, wrapping if necessary.
 * If the text exceeds the given number of lines, it will be truncated by displaying an ellipsis
 * It must be greater than zero.
 */
@Composable
fun HtmlTextView(
    text: Spanned,
    modifier: Modifier = Modifier,
    textAppearanceResId: Int? = null,
    color: Color = Color.White,
    maxLines: Int = Int.MAX_VALUE
) {
    AndroidView(
        modifier = modifier,
        factory = {
            MaterialTextView(it).apply {
                ellipsize = TextUtils.TruncateAt.END
                textAppearanceResId?.let(::setTextAppearance)
                setTextColor(color.toArgb())
                // links
                autoLinkMask = Linkify.WEB_URLS
                linksClickable = true
                setLinkTextColor(Color.White.toArgb())
                this.maxLines = maxLines
            }
        },
        update = { it.text = text }
    )
}
