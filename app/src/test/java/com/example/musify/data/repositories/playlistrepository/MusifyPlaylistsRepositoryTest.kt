package com.example.musify.data.repositories.playlistrepository

import com.example.musify.data.encoder.TestBase64Encoder
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.token.tokenmanager.TokenManager
import com.example.musify.data.repositories.tokenrepository.SpotifyTokenRepository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.utils.defaultMusifyJacksonConverterFactory
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class MusifyPlaylistsRepositoryTest {
    private lateinit var musifyPlaylistsRepository: MusifyPlaylistsRepository

    @Before
    fun setUp() {
        val spotifyService = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(defaultMusifyJacksonConverterFactory)
            .build()
            .create(SpotifyService::class.java)
        val tokenManager = Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/")
            .addConverterFactory(defaultMusifyJacksonConverterFactory)
            .build()
            .create(TokenManager::class.java)
        musifyPlaylistsRepository = MusifyPlaylistsRepository(
            tokenRepository = SpotifyTokenRepository(
                tokenManager,
                TestBase64Encoder()
            ),
            spotifyService = spotifyService
        )
    }

    @Test
    fun fetchPlaylistTest_validPlaylistId_isSuccessfullyFetched() {
        // (WARNING!: The playlist is a real playlist
        // and it might've been removed by the user)
        // given an valid playlistId
        val playlistId = "7sZbq8QGyMnhKPcLJvCUFD"
        // when fetching the playlist
        val result = runBlocking {
            musifyPlaylistsRepository.fetchPlaylistWithId(playlistId, "IN")
        }
        // the return type must be of type FetchedResource.Success
        assert(result is FetchedResource.Success)
    }
}