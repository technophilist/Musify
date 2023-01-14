package com.example.musify.usecases.getCurrentlyPlayingEpisodePlaybackStateUseCase

import com.example.musify.domain.PodcastEpisode
import com.example.musify.musicplayer.MusicPlayerV2
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class MusifyGetCurrentlyPlayingEpisodePlaybackStateUseCase @Inject constructor(
    musicPlayerV2: MusicPlayerV2
) : GetCurrentlyPlayingEpisodePlaybackStateUseCase {

    override val currentlyPlayingEpisodePlaybackStateStream = musicPlayerV2
        .currentPlaybackStateStream
        .mapNotNull {
            // check if the currently playing streamable is an instance of PodcastEpisode if, and
            // only if, the currently playing streamable is not null. If it is null, it might
            // mean that the playback state is ended,idle or loading.
            if (it.currentlyPlayingStreamable != null && it.currentlyPlayingStreamable !is PodcastEpisode) {
                return@mapNotNull null
            }
            when (it) {
                is MusicPlayerV2.PlaybackState.Ended,
                is MusicPlayerV2.PlaybackState.Error -> GetCurrentlyPlayingEpisodePlaybackStateUseCase.PlaybackState.Ended
                is MusicPlayerV2.PlaybackState.Loading -> GetCurrentlyPlayingEpisodePlaybackStateUseCase.PlaybackState.Loading
                is MusicPlayerV2.PlaybackState.Paused -> {
                    GetCurrentlyPlayingEpisodePlaybackStateUseCase.PlaybackState.Paused(it.currentlyPlayingStreamable as PodcastEpisode)
                }
                is MusicPlayerV2.PlaybackState.Playing -> {
                    GetCurrentlyPlayingEpisodePlaybackStateUseCase.PlaybackState.Playing(it.currentlyPlayingStreamable as PodcastEpisode)
                }
                is MusicPlayerV2.PlaybackState.Idle -> null
            }
        }
}