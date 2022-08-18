package com.example.musify.usecases.downloadDrawableFromUrlUseCase

import android.app.Application
import android.graphics.drawable.Drawable

fun interface DownloadDrawableFromUrlUseCase {
    suspend fun invoke(
        urlString: String,
        application: Application
    ): Result<Drawable>
}