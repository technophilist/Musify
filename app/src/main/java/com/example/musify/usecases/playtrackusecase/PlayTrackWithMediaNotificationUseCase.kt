package com.example.musify.usecases.playtrackusecase

import com.example.musify.domain.SearchResult

/**
 * A use case that is responsible for playing an instance of
 * [SearchResult.TrackSearchResult] and displaying
 * a media notification.
 */
interface PlayTrackWithMediaNotificationUseCase {
    /**
     * Used to play an instance of [SearchResult.TrackSearchResult].
     * @param onLoading the callback that will be executed when the image
     * for the notification is loading.
     * @param onFinishedLoading the callback that will be executed after the image
     * for the notification has finished loading. This callback receives a [Throwable]
     * as parameter, indicating whether the image that was fetched using the
     * [SearchResult.TrackSearchResult.imageUrlString] was successfully loaded and the
     * notification was sent. **Note, if the image wasn't loaded successfully, the track
     * won't be played.** Both [onLoading] and [onFinishedLoading] **will be called on the
     * main thread.**
     */
    suspend fun invoke(
        track: SearchResult.TrackSearchResult,
        onLoading: () -> Unit,
        onFinishedLoading: (Throwable?) -> Unit,
    )
}