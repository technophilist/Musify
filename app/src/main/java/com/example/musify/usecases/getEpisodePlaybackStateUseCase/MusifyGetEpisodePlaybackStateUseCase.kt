package com.example.musify.usecases.getEpisodePlaybackStateUseCase

import com.example.musify.domain.PodcastEpisode
import com.example.musify.musicplayer.MusicPlayerV2
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class MusifyGetEpisodePlaybackStateUseCase @Inject constructor(
    musicPlayerV2: MusicPlayerV2
) : GetEpisodePlaybackStateUseCase {

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
                is MusicPlayerV2.PlaybackState.Error -> GetEpisodePlaybackStateUseCase.PlaybackState.Ended
                is MusicPlayerV2.PlaybackState.Loading -> GetEpisodePlaybackStateUseCase.PlaybackState.Loading
                is MusicPlayerV2.PlaybackState.Paused -> {
                    GetEpisodePlaybackStateUseCase.PlaybackState.Paused(it.currentlyPlayingStreamable as PodcastEpisode)
                }
                is MusicPlayerV2.PlaybackState.Playing -> {
                    GetEpisodePlaybackStateUseCase.PlaybackState.Playing(it.currentlyPlayingStreamable as PodcastEpisode)
                }
                is MusicPlayerV2.PlaybackState.Idle -> null
            }
        }
}