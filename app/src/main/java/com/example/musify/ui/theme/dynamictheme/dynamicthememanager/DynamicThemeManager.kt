package com.example.musify.ui.theme.dynamictheme.dynamicthememanager

import android.content.Context
import androidx.compose.ui.graphics.Color

interface DynamicThemeManager {
    suspend fun getBackgroundColorForImageFromUrl(
        url: String,
        context: Context
    ): Color?
}