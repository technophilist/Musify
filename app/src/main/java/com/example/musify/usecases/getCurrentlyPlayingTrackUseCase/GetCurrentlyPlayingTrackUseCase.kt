package com.example.musify.usecases.getCurrentlyPlayingTrackUseCase

import com.example.musify.domain.SearchResult
import kotlinx.coroutines.flow.Flow

interface GetCurrentlyPlayingTrackUseCase {
    fun getCurrentlyPlayingTrackStream(): Flow<SearchResult.TrackSearchResult?>
}