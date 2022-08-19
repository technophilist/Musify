package com.example.musify.usecases.downloadDrawableFromUrlUseCase

import android.app.Application
import android.graphics.drawable.Drawable
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.musify.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusifyDownloadDrawableFromUrlUseCase @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : DownloadDrawableFromUrlUseCase {

    override suspend fun invoke(
        urlString: String,
        application: Application
    ): Result<Drawable> = withContext(ioDispatcher) {
        val imageRequest = ImageRequest.Builder(application)
            .data(urlString)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
        when (val imageResult = ImageLoader(application).execute(imageRequest)) {
            is SuccessResult -> Result.success(imageResult.drawable)
            is ErrorResult -> Result.failure(imageResult.throwable)
        }
    }

}