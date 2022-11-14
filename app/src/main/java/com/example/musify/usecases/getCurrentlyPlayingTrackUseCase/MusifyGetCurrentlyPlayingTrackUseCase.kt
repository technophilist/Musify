package com.example.musify.usecases.getCurrentlyPlayingTrackUseCase

import com.example.musify.domain.SearchResult
import com.example.musify.musicplayer.MusicPlayer
import com.example.musify.musicplayer.MusicPlayerV2
import com.example.musify.musicplayer.utils.toTrackSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class MusifyGetCurrentlyPlayingTrackUseCase @Inject constructor(
    val musicPlayer: MusicPlayerV2
) : GetCurrentlyPlayingTrackUseCase {
    override fun getCurrentlyPlayingTrackStream(): Flow<SearchResult.TrackSearchResult> =
        musicPlayer.currentPlaybackStateStream
            .filterIsInstance<MusicPlayer.PlaybackState.Playing>()
            .mapNotNull { it.currentlyPlayingTrack.toTrackSearchResult() }
}