package com.example.musify.usecases.playtrackusecase

import android.content.Context
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult
import com.example.musify.di.IODispatcher
import com.example.musify.di.MainDispatcher
import com.example.musify.domain.SearchResult
import com.example.musify.domain.toMusicPlayerTrack
import com.example.musify.musicplayer.MusicPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusifyPlayTrackWithMediaNotificationUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicPlayer: MusicPlayer,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : PlayTrackWithMediaNotificationUseCase {
    private suspend fun downloadBitmapFromUrl(urlString: String): ImageResult {
        val imageRequest = ImageRequest.Builder(context)
            .data(urlString)
            .build()
        return ImageLoader(context).execute(imageRequest)
    }

    // TODO docs -  mention that callbacks will be called on the main thread
    override suspend fun invoke(
        track: SearchResult.TrackSearchResult,
        onLoading: () -> Unit,
        onFinishedLoading: (Throwable?) -> Unit,
    ) {
        onLoading()
        val downloadAlbumArtResult = withContext(ioDispatcher) {
            downloadBitmapFromUrl(track.imageUrlString)
        }
        withContext(mainDispatcher) {
            if (downloadAlbumArtResult is SuccessResult) {
                musicPlayer.playTrack(track.toMusicPlayerTrack(downloadAlbumArtResult.drawable.toBitmap()))
                onFinishedLoading(null)
            } else {
                onFinishedLoading((downloadAlbumArtResult as ErrorResult).throwable)
            }
        }
    }
}