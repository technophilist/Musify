package com.example.musify.ui.components

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musify.R
import com.example.musify.ui.theme.MusifyTheme
import com.example.musify.ui.theme.dynamictheme.DynamicBackgroundType
import com.example.musify.ui.theme.dynamictheme.DynamicThemeResource
import com.example.musify.ui.theme.dynamictheme.DynamicallyThemedSurface

/**
 * An appbar that is meant to be used in a detail screen. It is mainly
 * used to display the [title] with a back button.
 * @param title the title to be displayed.
 * @param onBackButtonClicked the lambda to execute with the user clicks
 * on the back button.
 * @param modifier the modifier to be applied to the app bar.
 * @param dynamicThemeResource the resource to be used to set the
 * background color. By default, it is set to [DynamicThemeResource.Empty].
 */
@Composable
fun DetailScreenTopAppBar(
    title: String,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    dynamicThemeResource: DynamicThemeResource = DynamicThemeResource.Empty,
) {
    val dynamicThemeBackgroundType = remember {
        DynamicBackgroundType.Filled(scrimColor = Color.Black.copy(alpha = 0.3f))
    }
    DynamicallyThemedSurface(
        modifier = Modifier.shadow(elevation = AppBarDefaults.TopAppBarElevation),
        dynamicThemeResource = dynamicThemeResource,
        dynamicBackgroundType = dynamicThemeBackgroundType
    ) {
        // Since the top app bar is laid on top of a dynamically
        // themed surface, any elevation would make the app bar
        // look like it has a border. Therefore, set the elevation
        // to 0dp.
        TopAppBar(
            modifier = modifier,
            backgroundColor = Color.Transparent,
            elevation = 0.dp
        ) {
            IconButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
                    .offset(y = 1.dp),
                onClick = onBackButtonClicked
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_chevron_left_24),
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun DetailScreenTopAppBarPreview() {
    MusifyTheme {
        DetailScreenTopAppBar(
            title = "Title",
            onBackButtonClicked = {}
        )
    }
}