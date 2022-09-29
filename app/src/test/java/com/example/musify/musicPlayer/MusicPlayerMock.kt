package com.example.musify.musicPlayer

import com.example.musify.musicplayer.MusicPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class MusicPlayerMock : MusicPlayer {
    private lateinit var onPlaybackStateChangedCallback: (MusicPlayer.PlaybackState) -> Unit
    private var currentlyPlayingTrack: MusicPlayer.Track? = null
    private var isPlaybackPaused = false
    override fun pauseCurrentlyPlayingTrack() {
        val pausedState = currentlyPlayingTrack?.let(MusicPlayer.PlaybackState::Paused) ?: return
        onPlaybackStateChangedCallback(pausedState)
    }

    override fun stopPlayingTrack() {
        onPlaybackStateChangedCallback(MusicPlayer.PlaybackState.Idle)
    }

    override fun playTrack(track: MusicPlayer.Track) {
        if (currentlyPlayingTrack == track) {
            return
        }
        currentlyPlayingTrack = track
        onPlaybackStateChangedCallback(
            MusicPlayer.PlaybackState.Playing(
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

    override fun addOnPlaybackStateChangedListener(onPlaybackStateChanged: (MusicPlayer.PlaybackState) -> Unit) {
        onPlaybackStateChangedCallback = onPlaybackStateChanged
        onPlaybackStateChangedCallback(MusicPlayer.PlaybackState.Idle)
    }

    override fun removeListenersIfAny() {}
}