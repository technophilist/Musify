package com.example.musify.usecases.getPlaybackLoadingStatusUseCase

import kotlinx.coroutines.flow.Flow

@Deprecated("Use GetCurrentlyPlayingStreamable")
interface GetPlaybackLoadingStatusUseCase {
    val loadingStatusStream: Flow<Boolean>
}