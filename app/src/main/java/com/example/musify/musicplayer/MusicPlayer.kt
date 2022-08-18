package com.example.musify.musicplayer

import android.graphics.Bitmap

interface MusicPlayer {
    sealed class PlaybackState {
        data class Playing(val currentlyPlayingTrack: Track) : PlaybackState()
        object Paused : PlaybackState()
        object Stopped : PlaybackState()
        object Error : PlaybackState()
        object Idle : PlaybackState()
    }

    fun pauseCurrentlyPlayingTrack()
    fun stopPlayingTrack()
    fun playTrack(track: Track)
    data class Track(
        val id: String,
        val title: String,
        val artistsString: String,
        val albumArt: Bitmap,
        val trackUrlString: String
    )

    fun addOnPlaybackStateChangedListener(onPlaybackStateChanged: (PlaybackState) -> Unit)
    fun removeListenersIfAny()
}