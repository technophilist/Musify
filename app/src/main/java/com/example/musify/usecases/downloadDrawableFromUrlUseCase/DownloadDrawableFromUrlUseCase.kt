package com.example.musify.usecases.downloadDrawableFromUrlUseCase

import android.content.Context
import android.graphics.drawable.Drawable

fun interface DownloadDrawableFromUrlUseCase {
    suspend fun invoke(
        urlString: String,
        context: Context
    ): Result<Drawable>
}