package com.example.musify.musicplayer

import android.graphics.Bitmap

interface MusicPlayer {
    enum class PlaybackState { PLAYING, PAUSED, STOPPED, ERROR }

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