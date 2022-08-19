package com.example.musify.musicPlayer

import com.example.musify.musicplayer.MusicPlayer

class MusicPlayerMock : MusicPlayer {
    private lateinit var onPlaybackStateChangedCallback: (MusicPlayer.PlaybackState) -> Unit

    override fun pauseCurrentlyPlayingTrack() {
        onPlaybackStateChangedCallback(MusicPlayer.PlaybackState.Paused)
    }

    override fun stopPlayingTrack() {
        onPlaybackStateChangedCallback(MusicPlayer.PlaybackState.Stopped)
    }

    override fun playTrack(track: MusicPlayer.Track) {
        onPlaybackStateChangedCallback(MusicPlayer.PlaybackState.Playing(track))
    }

    override fun addOnPlaybackStateChangedListener(onPlaybackStateChanged: (MusicPlayer.PlaybackState) -> Unit) {
        onPlaybackStateChangedCallback = onPlaybackStateChanged
        onPlaybackStateChangedCallback(MusicPlayer.PlaybackState.Idle)
    }

    override fun removeListenersIfAny() {}
}