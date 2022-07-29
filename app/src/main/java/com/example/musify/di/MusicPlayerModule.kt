package com.example.musify.di

import com.example.musify.musicplayer.MusicPlayer
import com.example.musify.musicplayer.MusifyBackgroundMusicPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class MusicPlayerModule {
    @Binds
    abstract fun bindMusicPlayer(
        musifyBackgroundMusicPlayer: MusifyBackgroundMusicPlayer
    ): MusicPlayer
}