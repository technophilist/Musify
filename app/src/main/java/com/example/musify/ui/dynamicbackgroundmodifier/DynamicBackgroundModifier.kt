package com.example.musify.ui.dynamicbackgroundmodifier

import androidx.compose.animation.animateColorAsState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import com.example.musify.ui.theme.dynamictheme.LocalDynamicThemeManager

/**
 * A sealed class hierarchy that contains classes representing the
 * different resources that could be used to extract the colors
 * for [Modifier.dynamicBackground].
 * Though this hierarchy contains only one meaningful class for
 * now, being a sealed interface, it makes adding of additional resources
 * easier. For example, in the future, we could add a class to
 * the hierarchy, that will directly take a bitmap as input.
 * This could be useful if an image has already been pre-fetched.
 */
sealed interface DynamicBackgroundResource {
    data class FromImageUrl(val url: String) : DynamicBackgroundResource
    object Empty : DynamicBackgroundResource
}

/**
 * A sealed class hierarchy containing different styles that could
 * be used with the [Modifier.dynamicBackground].
 */
sealed interface DynamicBackgroundStyle {
    /**
     * A gradient that fills the specified [fraction] of the maximum size of the screen,
     * between 0.0 and 1.0, inclusive.
     */
    data class Gradient(val fraction: Float = 1f) : DynamicBackgroundStyle

    /**
     * Fills the background based on the color extracted from [DynamicBackgroundResource].
     * A scrim can also be applied using the [scrimColor] param. The alpha of the scrim
     * can be specified using the [Color.copy] method.
     */
    data class Filled(val scrimColor: Color? = null) : DynamicBackgroundStyle
}

/**
 * A  modifier that sets the background of a composable based on the provided [dynamicBackgroundResource].
 * @param dynamicBackgroundResource the resource from which the colors should be extracted.
 * @param dynamicBackgroundStyle the style to apply for the background.
 */
fun Modifier.dynamicBackground(
    dynamicBackgroundResource: DynamicBackgroundResource = DynamicBackgroundResource.Empty,
    dynamicBackgroundStyle: DynamicBackgroundStyle = DynamicBackgroundStyle.Gradient()
) = composed {
    val context = LocalContext.current
    val themeManager = LocalDynamicThemeManager.current
    val defaultBackgroundColor = MaterialTheme.colors.background
    var backgroundColor by remember { mutableStateOf(defaultBackgroundColor) }
    val animatedBackgroundColor by animateColorAsState(targetValue = backgroundColor)
    val backgroundGradientColors = remember(animatedBackgroundColor) {
        listOf(
            animatedBackgroundColor,
            Color(0xFF121212),
        )
    }
    LaunchedEffect(dynamicBackgroundResource) {
        val newBackgroundColor = when (dynamicBackgroundResource) {
            DynamicBackgroundResource.Empty -> defaultBackgroundColor
            is DynamicBackgroundResource.FromImageUrl -> themeManager
                .getBackgroundColorForImageFromUrl(dynamicBackgroundResource.url, context)
                ?: return@LaunchedEffect
        }
        backgroundColor = newBackgroundColor
    }

    Modifier.drawBehind {
        // skip composition, measurement and layout phase
        // and just change the color in the drawing phase.
        when (dynamicBackgroundStyle) {
            is DynamicBackgroundStyle.Filled -> drawRectFilledWithColor(
                color = animatedBackgroundColor,
                scrimColor = dynamicBackgroundStyle.scrimColor
            )
            is DynamicBackgroundStyle.Gradient -> {
                drawRectWithGradient(backgroundGradientColors, dynamicBackgroundStyle.fraction)
            }
        }
    }
}


private fun DrawScope.drawRectFilledWithColor(
    color: Color,
    scrimColor: Color? = null
) {
    drawRect(
        color = color,
        size = size
    )
    if (scrimColor != null) {
        drawRect(
            color = scrimColor,
            size = size
        )
    }
}

private fun DrawScope.drawRectWithGradient(backgroundGradientColors: List<Color>, fraction: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = backgroundGradientColors,
            endY = size.height * fraction
        ),
        size = size
    )
}