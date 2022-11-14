package com.example.musify.di

import com.example.musify.usecases.downloadDrawableFromUrlUseCase.DownloadDrawableFromUrlUseCase
import com.example.musify.usecases.downloadDrawableFromUrlUseCase.MusifyDownloadDrawableFromUrlUseCase
import com.example.musify.usecases.getCurrentlyPlayingTrackUseCase.GetCurrentlyPlayingTrackUseCase
import com.example.musify.usecases.getCurrentlyPlayingTrackUseCase.MusifyGetCurrentlyPlayingTrackUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class TrackUseCasesComponent {
    @Binds
    abstract fun bindDownloadDrawableFromUrlUseCase(
        impl: MusifyDownloadDrawableFromUrlUseCase
    ): DownloadDrawableFromUrlUseCase

    @Binds
    abstract fun bindGetCurrentlyPlayingTrackUseCase(
        impl: MusifyGetCurrentlyPlayingTrackUseCase
    ): GetCurrentlyPlayingTrackUseCase
}