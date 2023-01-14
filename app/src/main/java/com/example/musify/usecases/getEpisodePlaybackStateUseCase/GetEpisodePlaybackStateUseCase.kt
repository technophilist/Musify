package com.example.musify.usecases.getEpisodePlaybackStateUseCase

import com.example.musify.domain.PodcastEpisode
import kotlinx.coroutines.flow.Flow

interface GetEpisodePlaybackStateUseCase {
    sealed interface PlaybackState {
        data class Playing(val playingEpisode: PodcastEpisode) : PlaybackState
        data class Paused(val pausedEpisode: PodcastEpisode) : PlaybackState
        object Loading : PlaybackState
        object Ended : PlaybackState
    }

    val currentlyPlayingEpisodePlaybackStateStream: Flow<PlaybackState>
}