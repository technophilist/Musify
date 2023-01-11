package com.example.musify.usecases.getCurrentlyPlayingPodcastEpisodeUseCase

import com.example.musify.domain.PodcastEpisode
import kotlinx.coroutines.flow.Flow

@Deprecated("Use GetCurrentlyPlayingStreamable")
interface GetCurrentlyPlayingPodcastEpisodeUseCase {
    fun getCurrentlyPlayingPodcastEpisodeStream(): Flow<PodcastEpisode?>
}