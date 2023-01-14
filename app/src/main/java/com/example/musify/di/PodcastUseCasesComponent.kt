package com.example.musify.di

import com.example.musify.usecases.getCurrentlyPlayingEpisodePlaybackStateUseCase.GetCurrentlyPlayingEpisodePlaybackStateUseCase
import com.example.musify.usecases.getCurrentlyPlayingEpisodePlaybackStateUseCase.MusifyGetCurrentlyPlayingEpisodePlaybackStateUseCase
import com.example.musify.usecases.getCurrentlyPlayingStreamableUseCase.GetCurrentlyPlayingStreamableUseCase
import com.example.musify.usecases.getCurrentlyPlayingStreamableUseCase.MusifyGetCurrentlyPlayingStreamableUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class PodcastUseCasesComponent {
    @Binds
    abstract fun bindGetCurrentlyPlayingStreamableUseCase(
        impl: MusifyGetCurrentlyPlayingStreamableUseCase
    ): GetCurrentlyPlayingStreamableUseCase

    @Binds
    abstract fun bindGetEpisodePlaybackStateUseCase(
        impl: MusifyGetCurrentlyPlayingEpisodePlaybackStateUseCase
    ): GetCurrentlyPlayingEpisodePlaybackStateUseCase
}