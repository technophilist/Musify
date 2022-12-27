package com.example.musify.musicplayer

import android.graphics.Bitmap
import com.example.musify.domain.Streamable
import kotlinx.coroutines.flow.Flow

interface MusicPlayerV2 {
    sealed class PlaybackState {
        data class Loading(val previouslyPlayingStreamable: Streamable?) : PlaybackState()
        data class Playing(
            val currentlyPlayingStreamable: Streamable,
            val totalDuration: Long,
            val currentPlaybackPositionInMillisFlow: Flow<Long>
        ) : PlaybackState()

        data class Paused(val currentlyPlayingStreamable: Streamable) : PlaybackState()
        data class Ended(val streamable: Streamable) : PlaybackState()
        object Error : PlaybackState()
        object Idle : PlaybackState()
    }

    val currentPlaybackStateStream: Flow<PlaybackState>
    fun playStreamable(streamable: Streamable, associatedAlbumArt: Bitmap)
    fun pauseCurrentlyPlayingTrack()
    fun stopPlayingTrack()
    fun tryResume(): Boolean
}