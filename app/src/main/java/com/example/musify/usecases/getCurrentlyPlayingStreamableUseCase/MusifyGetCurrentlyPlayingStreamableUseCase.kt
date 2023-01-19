package com.example.musify.usecases.getCurrentlyPlayingStreamableUseCase

import com.example.musify.domain.Streamable
import com.example.musify.musicplayer.MusicPlayerV2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MusifyGetCurrentlyPlayingStreamableUseCase @Inject constructor(
    musicPlayer: MusicPlayerV2
) : GetCurrentlyPlayingStreamableUseCase {
    override val currentlyPlayingStreamableStream: Flow<Streamable> = musicPlayer
        .currentPlaybackStateStream
        .filterIsInstance<MusicPlayerV2.PlaybackState.Playing>()
        .map { it.currentlyPlayingStreamable }
}