package com.example.musify.di

import com.example.musify.data.repositories.albumsrepository.AlbumsRepository
import com.example.musify.data.repositories.albumsrepository.MusifyAlbumsRepository
import com.example.musify.data.repositories.genresrepository.GenresRepository
import com.example.musify.data.repositories.genresrepository.MusifyGenresRepository
import com.example.musify.data.repositories.homefeedrepository.HomeFeedRepository
import com.example.musify.data.repositories.homefeedrepository.MusifyHomeFeedRepository
import com.example.musify.data.repositories.podcastsrepository.MusifyPodcastsRepository
import com.example.musify.data.repositories.podcastsrepository.PodcastsRepository
import com.example.musify.data.repositories.searchrepository.MusifySearchRepository
import com.example.musify.data.repositories.searchrepository.SearchRepository
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
    abstract fun bindGeneresRepository(impl: MusifyGenresRepository): GenresRepository

    @Binds
    abstract fun bindSearchRepository(impl: MusifySearchRepository): SearchRepository

    @Binds
    abstract fun bindHomeFeedRepository(impl: MusifyHomeFeedRepository): HomeFeedRepository

    @Binds
    abstract fun bindPodcastsRepository(impl: MusifyPodcastsRepository): PodcastsRepository
}