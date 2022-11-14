package com.example.musify.musicplayer

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface MusicPlayerV2 {
    sealed class PlaybackState {
        data class Playing(
            val currentlyPlayingTrack: Track,
            val totalDuration: Long,
            val currentPlaybackPositionInMillisFlow: Flow<Long>
        ) : PlaybackState()

        data class Paused(val currentlyPlayingTrack: Track) : PlaybackState()
        data class Ended(val track: Track) : PlaybackState()
        object Error : PlaybackState()
        object Idle : PlaybackState()
    }

    data class Track(
        val id: String,
        val title: String,
        val artistsString: String,
        val albumArt: Bitmap,
        val albumArtUrlString: String,
        val trackUrlString: String
    )

    val currentPlaybackStateStream: Flow<PlaybackState>
    fun playTrack(track: Track)
    fun pauseCurrentlyPlayingTrack()
    fun stopPlayingTrack()
    fun tryResume(): Boolean
}