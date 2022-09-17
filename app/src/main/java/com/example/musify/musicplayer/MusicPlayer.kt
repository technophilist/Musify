package com.example.musify.musicplayer

import android.graphics.Bitmap

interface MusicPlayer {
    sealed class PlaybackState {
        data class Playing(val currentlyPlayingTrack: Track) : PlaybackState()
        data class Paused(val currentlyPlayingTrack: Track) : PlaybackState()
        object Error : PlaybackState()
        object Idle : PlaybackState()
    }

    fun pauseCurrentlyPlayingTrack()
    fun stopPlayingTrack()
    fun playTrack(track: Track)
    fun tryResume():Boolean
    fun addOnPlaybackStateChangedListener(onPlaybackStateChanged: (PlaybackState) -> Unit)
    fun removeListenersIfAny()

    data class Track(
        val id: String,
        val title: String,
        val artistsString: String,
        val albumArt: Bitmap,
        val albumArtUrlString: String,
        val trackUrlString: String
    )
}