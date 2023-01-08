package com.example.musify.usecases.getCurrentlyPlayingTrackUseCase

import com.example.musify.domain.SearchResult
import kotlinx.coroutines.flow.Flow

@Deprecated("Use GetCurrentlyPlayingStreamable")
interface GetCurrentlyPlayingTrackUseCase {
    fun getCurrentlyPlayingTrackStream(): Flow<SearchResult.TrackSearchResult?>
}