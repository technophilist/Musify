package com.example.musify.usecases.downloadDrawableFromUrlUseCase

import android.content.Context
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
        context: Context
    ): Result<Drawable> = withContext(ioDispatcher) {
        val imageRequest = ImageRequest.Builder(context)
            .data(urlString)
            .allowHardware(false)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
        when (val imageResult = ImageLoader(context).execute(imageRequest)) {
            is SuccessResult -> Result.success(imageResult.drawable)
            is ErrorResult -> Result.failure(imageResult.throwable)
        }
    }

}