package com.example.musify.di

import com.example.musify.musicplayer.MusicPlayer
import com.example.musify.musicplayer.MusifyBackgroundMusicPlayer
import com.example.musify.usecases.playtrackusecase.MusifyPlayTrackWithMediaNotificationUseCase
import com.example.musify.usecases.playtrackusecase.PlayTrackWithMediaNotificationUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Note: The dependencies are not scoped because the underlying
 * media player is always a singleton. [ExoPlayerModule.provideExoplayer]
 * is annotated with @Singleton, therefore any class that depends on it
 * need to be a singleton since the class will be provided the same
 * instance.
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class MusicPlayerModule {
    @Binds
    abstract fun bindMusicPlayer(
        musifyBackgroundMusicPlayer: MusifyBackgroundMusicPlayer
    ): MusicPlayer

    @Binds
    abstract fun bindPlayTrackWithMediaNotificationUseCase(
        impl: MusifyPlayTrackWithMediaNotificationUseCase
    ): PlayTrackWithMediaNotificationUseCase
}