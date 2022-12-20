package com.example.musify.musicplayer

import android.graphics.Bitmap
import com.example.musify.domain.SearchResult
import com.example.musify.musicplayer.utils.toTrackSearchResult
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

    /**
     * A class that represents a [Track] playable by a concrete
     * implementation of [MusicPlayerV2].
     * Note: This class has both [albumArt] and [albumArtUrlString] as
     * properties. It might seem redundant to have both [albumArtUrlString]
     * since the the [albumArt]. The main reason as to why [albumArtUrlString]
     * exists is because this information can be used when converting an instance
     * of [Track] to some other domain object. For example, [albumArtUrlString]
     * will be used when converting an instance of [Track] to an instance of
     * [SearchResult.TrackSearchResult] using the [toTrackSearchResult].
     */
    data class Track(
        val id: String,
        val title: String,
        val subtitle: String,
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