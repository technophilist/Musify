package com.example.musify.musicPlayer

import android.graphics.Bitmap
import com.example.musify.domain.Streamable
import com.example.musify.domain.fakeTrackSearchResult
import com.example.musify.musicplayer.MusicPlayerV2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

class MusicPlayerMock : MusicPlayerV2 {
    private var currentlyPlayingStreamable: Streamable? = null
    private val _currentPlaybackStateStream =
        MutableStateFlow<MusicPlayerV2.PlaybackState>(MusicPlayerV2.PlaybackState.Idle)
    override val currentPlaybackStateStream =
        _currentPlaybackStateStream as Flow<MusicPlayerV2.PlaybackState>

    override fun pauseCurrentlyPlayingTrack() {
        _currentPlaybackStateStream.value =
            currentlyPlayingStreamable?.let(MusicPlayerV2.PlaybackState::Paused) ?: return
    }

    override fun stopPlayingTrack() {
        _currentPlaybackStateStream.value =
            currentlyPlayingStreamable?.let(MusicPlayerV2.PlaybackState::Ended) ?: return
    }


    override fun playStreamable(streamable: Streamable, associatedAlbumArt: Bitmap) {
        currentlyPlayingStreamable = streamable
        _currentPlaybackStateStream.value = MusicPlayerV2.PlaybackState.Playing(
            currentlyPlayingStreamable = fakeTrackSearchResult,
            totalDuration = 10L,
            currentPlaybackPositionInMillisFlow = emptyFlow()
        )

    }

    override fun tryResume(): Boolean = currentlyPlayingStreamable != null

}