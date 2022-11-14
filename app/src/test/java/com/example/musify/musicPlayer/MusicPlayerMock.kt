package com.example.musify.musicPlayer

import com.example.musify.musicplayer.MusicPlayerV2
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class MusicPlayerMock : MusicPlayerV2 {
    private lateinit var onPlaybackStateChangedCallback: (MusicPlayerV2.PlaybackState) -> Unit
    private var currentlyPlayingTrack: MusicPlayerV2.Track? = null
    private var isPlaybackPaused = false
    override fun pauseCurrentlyPlayingTrack() {
        val pausedState = currentlyPlayingTrack?.let(MusicPlayerV2.PlaybackState::Paused) ?: return
        onPlaybackStateChangedCallback(pausedState)
    }

    override fun stopPlayingTrack() {
        onPlaybackStateChangedCallback(MusicPlayerV2.PlaybackState.Idle)
    }

    override val currentPlaybackStateStream: Flow<MusicPlayerV2.PlaybackState>
        get() = TODO("Not yet implemented")

    override fun playTrack(track: MusicPlayerV2.Track) {
        if (currentlyPlayingTrack == track) {
            return
        }
        currentlyPlayingTrack = track
        onPlaybackStateChangedCallback(
            MusicPlayerV2.PlaybackState.Playing(
                currentlyPlayingTrack = track,
                totalDuration = 5_000,
                currentPlaybackPositionInMillisFlow = flow {
                    for (i in 1..5) {
                        emit(i * 1_000L)
                        while (isPlaybackPaused) emit(i * 1000L)
                        delay(1_000)
                    }
                }.distinctUntilChanged()
            )
        )
    }

    override fun tryResume(): Boolean {
        return currentlyPlayingTrack != null
    }

}