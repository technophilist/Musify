package com.example.musify.di

import com.example.musify.data.encoder.AndroidBase64Encoder
import com.example.musify.data.encoder.Base64Encoder
import com.example.musify.data.repositories.tokenrepository.SpotifyTokenRepository
import com.example.musify.data.repositories.tokenrepository.TokenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationModule {

    @Binds
    abstract fun bindBase64Encoder(
        androidBase64Encoder: AndroidBase64Encoder
    ): Base64Encoder

    @Binds
    @Singleton
    abstract fun bindTokenRepository(
        spotifyTokenRepository: SpotifyTokenRepository
    ): TokenRepository
}