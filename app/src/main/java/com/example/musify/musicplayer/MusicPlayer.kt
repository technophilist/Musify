package com.example.musify.musicplayer

interface MusicPlayer {
    fun playTrackFromUrlString(urlString: String)
    fun pauseCurrentlyPlayingTrack()
    fun stopPlayingTrack()
}