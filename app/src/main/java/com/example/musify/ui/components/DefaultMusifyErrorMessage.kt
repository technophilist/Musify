package com.example.musify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

/**
 * A composable that is used to display an error message through-out
 * the app with the specified [title] and [subtitle]. It manages the
 * styling of the both the [title] and [subtitle].
 * @param modifier the modifier to be applied to the composable.
 */
@Composable
fun DefaultMusifyErrorMessage(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            style = LocalTextStyle.current.copy(
                color = Color.White.copy(alpha = ContentAlpha.disabled)
            ),
            textAlign = TextAlign.Center
        )
    }
}