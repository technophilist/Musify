package com.example.musify.ui.dynamicTheme.dynamicbackgroundmodifier

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.musify.ui.dynamicTheme.manager.DynamicThemeManager
import com.example.musify.ui.dynamicTheme.manager.MusifyDynamicThemeManager
import com.example.musify.usecases.downloadDrawableFromUrlUseCase.MusifyDownloadDrawableFromUrlUseCase
import kotlinx.coroutines.Dispatchers

val LocalDynamicThemeManager: ProvidableCompositionLocal<DynamicThemeManager> =
    staticCompositionLocalOf {
        MusifyDynamicThemeManager(
            downloadDrawableFromUrlUseCase = MusifyDownloadDrawableFromUrlUseCase(Dispatchers.IO),
            defaultDispatcher = Dispatchers.IO
        )
    }