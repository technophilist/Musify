package com.example.musify.usecases.getCurrentlyPlayingTrackUseCase

import com.example.musify.domain.SearchResult
import com.example.musify.musicplayer.MusicPlayerV2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MusifyGetCurrentlyPlayingTrackUseCase @Inject constructor(
    val musicPlayer: MusicPlayerV2
) : GetCurrentlyPlayingTrackUseCase {
    override fun getCurrentlyPlayingTrackStream(): Flow<SearchResult.TrackSearchResult> =
        musicPlayer.currentPlaybackStateStream
            .filterIsInstance<MusicPlayerV2.PlaybackState.Playing>()
            .filter { it.currentlyPlayingStreamable is SearchResult.TrackSearchResult }
            .map { it.currentlyPlayingStreamable as SearchResult.TrackSearchResult }
}