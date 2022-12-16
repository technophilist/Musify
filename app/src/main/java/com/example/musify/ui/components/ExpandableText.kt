package com.example.musify.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

/**
 * An expandable text composable. The "expand" text button will appear
 * with the [expandButtonText] after the last line, if and only if the
 * text overflows.
 * @param text the text to be displayed.
 * @param expandButtonText the text to be displayed for the expand button.
 * @param modifier the modifier to be applied to the text composable.
 * @param style Style configuration for the text such as color, font, line height etc.
 * @param color the [Color] to apply to the text. If [Color.Unspecified], and style has no color set,
 * this will be LocalContentColor.
 * @param maxLines  An optional maximum number of lines for the text to span, wrapping if necessary.
 * If the text exceeds the given number of lines, it will be truncated by displaying an ellipsis
 * It must be greater than zero.
 */
@Composable
fun ExpandableText(
    text: String,
    expandButtonText:String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE
) {
    var didTextOverflow by remember { mutableStateOf(false) }
    var currentMaxLines by remember { mutableStateOf(maxLines) }
    Column(modifier = Modifier.animateContentSize(animationSpec = tween())) {
        Text(
            modifier = modifier,
            text = text,
            maxLines = currentMaxLines,
            style = style,
            color = color,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { didTextOverflow = it.hasVisualOverflow }
        )
        if (didTextOverflow) {
            Text(
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { currentMaxLines = Int.MAX_VALUE },
                text = expandButtonText,
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}