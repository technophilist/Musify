package com.example.musify.musicplayer

import kotlinx.coroutines.flow.Flow

interface MusicPlayerV2 {
    val currentPlaybackStateStream: Flow<MusicPlayer.PlaybackState>
    fun playTrack(track: MusicPlayer.Track)
    fun pauseCurrentlyPlayingTrack()
    fun stopPlayingTrack()
    fun tryResume(): Boolean
}