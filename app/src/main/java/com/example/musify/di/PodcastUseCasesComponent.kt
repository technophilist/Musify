package com.example.musify.di

import com.example.musify.usecases.getCurrentlyPlayingPodcastEpisodeUseCase.GetCurrentlyPlayingPodcastEpisodeUseCase
import com.example.musify.usecases.getCurrentlyPlayingPodcastEpisodeUseCase.MusifyGetCurrentlyPlayingPodcastEpisodeUseCase
import dagger.Binds
import dagger.Component
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Component
@InstallIn(ViewModelComponent::class)
abstract class PodcastUseCasesComponent {
    @Binds
    abstract fun bindGetCurrentlyPlayingPodcastEpisodeUseCase(
        impl: MusifyGetCurrentlyPlayingPodcastEpisodeUseCase
    ): GetCurrentlyPlayingPodcastEpisodeUseCase
}