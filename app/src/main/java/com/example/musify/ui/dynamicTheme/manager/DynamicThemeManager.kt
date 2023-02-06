package com.example.musify.ui.dynamicTheme.manager

import android.content.Context
import androidx.compose.ui.graphics.Color

interface DynamicThemeManager {
    suspend fun getBackgroundColorForImageFromUrl(
        url: String,
        context: Context
    ): Color?
}