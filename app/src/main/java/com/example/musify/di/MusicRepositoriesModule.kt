package com.example.musify.di

import com.example.musify.data.repositories.albumsrepository.AlbumsRepository
import com.example.musify.data.repositories.albumsrepository.MusifyAlbumsRepository
import com.example.musify.data.repositories.artistsrepository.ArtistsRepository
import com.example.musify.data.repositories.artistsrepository.MusifyArtistsRepository
import com.example.musify.data.repositories.playlistrepository.MusifyPlaylistsRepository
import com.example.musify.data.repositories.playlistrepository.PlaylistsRepository
import com.example.musify.data.repositories.tracksrepository.MusifyTracksRepository
import com.example.musify.data.repositories.tracksrepository.TracksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class MusicRepositoriesModule {
    @Binds
    abstract fun bindTracksRepository(impl: MusifyTracksRepository): TracksRepository

    @Binds
    abstract fun bindAlbumsRepository(impl: MusifyAlbumsRepository): AlbumsRepository

    @Binds
    abstract fun bindArtistsRepository(impl: MusifyArtistsRepository): ArtistsRepository

    @Binds
    abstract fun bindPlaylistsRepository(impl: MusifyPlaylistsRepository): PlaylistsRepository

}