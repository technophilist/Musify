package com.example.musify.usecases.downloadDrawableFromUrlUseCase

import android.content.Context
import android.graphics.drawable.Drawable
import coil.imageLoader
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
        urlString: String, context: Context
    ): Result<Drawable> = withContext(ioDispatcher) {
        val imageRequest = ImageRequest.Builder(context)
            .data(urlString)
            .allowHardware(false)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
        // Each ImageLoader instance has its own memory & disk cache. Therefore,
        // use a singleton instead of creating an instance of ImageLoader
        // everytime this function is invoked.
        when (val imageResult = context.imageLoader.execute(imageRequest)) {
            is SuccessResult -> Result.success(imageResult.drawable)
            is ErrorResult -> Result.failure(imageResult.throwable)
        }
    }

}