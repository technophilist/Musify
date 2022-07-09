package com.example.musify.di

import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.token.tokenmanager.TokenManager
import com.example.musify.utils.defaultMusifyJacksonConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicServiceModule {
    @Provides
    @Singleton
    fun provideSpotifyService(): SpotifyService = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/") // TODO move const to separate file
        .addConverterFactory(defaultMusifyJacksonConverterFactory)
        .build()
        .create(SpotifyService::class.java)

    @Provides
    @Singleton
    fun provideTokenManager(): TokenManager = Retrofit.Builder()
        .baseUrl("https://accounts.spotify.com/")// TODO move const to separate file
        .addConverterFactory(defaultMusifyJacksonConverterFactory)
        .build()
        .create(TokenManager::class.java)
}