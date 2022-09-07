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
 * A surface that sets a background gradient based on the provided [dynamicThemeResource].
 * @param modifier the modifier to be applied to the surface.
 * @param fraction The fraction of the maximum size to use, between `0.0` and
 * `1.0`, inclusive.
 * @param content the content behind which the gradient background is to
 * be applied.
 */
@Composable
fun DynamicallyThemedSurface(
    dynamicThemeResource: DynamicThemeResource,
    modifier: Modifier = Modifier,
    fraction: Float = 1f,
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
    LaunchedEffect(dynamicThemeResource) {
        if (dynamicThemeResource !is DynamicThemeResource.FromImageUrl) return@LaunchedEffect
        val newBackgroundColor = themeManager
            .getBackgroundColorForImageFromUrl(dynamicThemeResource.url, context)
            ?: return@LaunchedEffect
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
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = backgroundGradientColors,
                        endY = size.height * fraction
                    ),
                    size = size
                )
            },
            content = { content() }
        )
    }
}