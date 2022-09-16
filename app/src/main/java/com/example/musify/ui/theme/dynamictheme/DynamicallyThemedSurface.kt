package com.example.musify.ui.theme.dynamictheme

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import com.example.musify.ui.theme.spotifyGreen


private val DarkColorPalette = darkColors(primary = spotifyGreen)

/**
 * A sealed class hierarchy that contains classes representing the
 * different resources that could be used to get the dynamic theme.
 * Thought this hierarchy contains only one meaningful class for
 * now, being a sealed class, it makes adding of additional resources
 * easier. For example, in the future, we could add a class to
 * the hierarchy, that will directly take a bitmap as input.
 * This could be useful if an image has already been pre-fetched.
 */
sealed class DynamicThemeResource {
    data class FromImageUrl(val url: String) : DynamicThemeResource()
    object Empty : DynamicThemeResource()
}

/**
 * A sealed class hierarchy that contains the different background types
 * associated with the [DynamicallyThemedSurface] composable.
 */
sealed class DynamicBackgroundType {
    /**
     * A gradient that fills the specified [fraction] of the maximum size of the screen,
     * between 0.0 and 1.0, inclusive.
     */
    data class Gradient(val fraction: Float = 1f) : DynamicBackgroundType()

    /**
     * Fills the background based on the color extracted from [DynamicThemeResource] passed
     * to the [DynamicallyThemedSurface].
     * A scrim can also be applied using the [scrimColor] param. The alpha of the scrim
     * can be specified using the [Color.copy] method.
     */
    data class Filled(val scrimColor: Color? = null) : DynamicBackgroundType()
}

/**
 * A surface that sets background based on the provided [dynamicThemeResource].
 * The type of background can be specified by using the [dynamicBackgroundType]
 * param.
 * @param modifier the modifier to be applied to the surface.
 * @param content the content behind which the gradient background is to
 * be applied.
 */
@Composable
fun DynamicallyThemedSurface(
    dynamicThemeResource: DynamicThemeResource,
    modifier: Modifier = Modifier,
    dynamicBackgroundType: DynamicBackgroundType = DynamicBackgroundType.Filled(),
    content: @Composable () -> Unit,
) {
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
    LaunchedEffect(dynamicThemeResource, defaultBackgroundColor) {
        val newBackgroundColor = when (dynamicThemeResource) {
            DynamicThemeResource.Empty -> defaultBackgroundColor
            is DynamicThemeResource.FromImageUrl -> themeManager
                .getBackgroundColorForImageFromUrl(dynamicThemeResource.url, context)
                ?: return@LaunchedEffect
        }
        backgroundColor = newBackgroundColor
    }

    MaterialTheme(colors = DarkColorPalette) {
        // using a box instead of surface because surface doesn't
        // allow backgrounds with gradients since any call to drawBehind
        // is overridden by the 'color' param of surface. Also,
        // the default implementation of surface itself uses the
        // Box composable.
        Box(
            modifier = modifier.drawBehind {
                // skip composition, measurement and layout phase
                // and just change the color in the drawing phase.
                when (dynamicBackgroundType) {
                    is DynamicBackgroundType.Gradient -> {
                        drawRectWithGradient(
                            backgroundGradientColors,
                            dynamicBackgroundType.fraction
                        )
                    }
                    is DynamicBackgroundType.Filled -> drawRectFilledWithColor(
                        color = animatedBackgroundColor,
                        scrimColor = dynamicBackgroundType.scrimColor
                    )
                }
            },
            content = { content() }
        )
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