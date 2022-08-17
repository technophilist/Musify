package com.example.musify.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.musify.R

/**
 * A composable that represents the default loading animation for the app.
 * @param isVisible indicates whether the loading animation is visible.
 * @param modifier the modifier to be applied to  [AnimatedVisibility]
 * used in this composable.
 */
@Composable
fun DefaultMusifyLoadingAnimation(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val loadingAnimationComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(
            R.raw.lottie_loading_anim
        )
    )
    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(5))
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                modifier = Modifier.size(96.dp),
                composition = loadingAnimationComposition,
                iterations = LottieConstants.IterateForever
            )
        }
    }
}