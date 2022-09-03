package com.example.musify.ui.theme.dynamictheme.dynamicthememanager

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.example.musify.usecases.downloadDrawableFromUrlUseCase.DownloadDrawableFromUrlUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class MusifyDynamicThemeManager(
    private val downloadDrawableFromUrlUseCase: DownloadDrawableFromUrlUseCase,
    private val defaultDispatcher: CoroutineDispatcher
) : DynamicThemeManager {
    private val colorCache = LruCache<String, Color>(10)
    private suspend fun getBackgroundColorForBitmap(bitmap: Bitmap): Color? =
        withContext(defaultDispatcher) {
            Palette.from(bitmap)
                .generate()
                .dominantSwatch
                ?.hsl
                ?.apply {
                    // increase the brightness of the dominant color by 50%
                    this[2] = 0.5f
                }
                ?.let(ColorUtils::HSLToColor)
                ?.let { Color(it) }
        }

    override suspend fun getBackgroundColorForImageFromUrl(url: String, context: Context): Color? {
        if (colorCache.get(url) != null) return colorCache.get(url)
        val bitmap = downloadDrawableFromUrlUseCase.invoke(url, context)
            .getOrNull()
            ?.toBitmap() ?: return null
        return getBackgroundColorForBitmap(bitmap).also { colorCache.put(url, it) }
    }
}

