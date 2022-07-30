package com.example.musify.usecases.playtrackusecase

import com.example.musify.domain.SearchResult

interface PlayTrackWithMediaNotificationUseCase {
    suspend fun invoke(
        track: SearchResult.TrackSearchResult,
        onLoading: () -> Unit,
        onFinishedLoading: (Throwable?) -> Unit,
    )
}