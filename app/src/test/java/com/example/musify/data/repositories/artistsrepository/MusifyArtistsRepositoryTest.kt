package com.example.musify.data.repositories.artistsrepository

import com.example.musify.data.encoder.TestBase64Encoder
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.token.tokenmanager.TokenManager
import com.example.musify.data.repositories.tokenrepository.SpotifyTokenRepository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.domain.MusifyErrorType
import com.example.musify.utils.defaultMusifyJacksonConverterFactory
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class MusifyArtistsRepositoryTest {

    private lateinit var artistsRepository: MusifyArtistsRepository

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
        artistsRepository = MusifyArtistsRepository(
            tokenRepository = SpotifyTokenRepository(
                tokenManager,
                TestBase64Encoder()
            ),
            spotifyService = spotifyService
        )
    }

    @Test
    fun artistFetchTest_validArtistId_isFetchedSuccessfully() = runBlocking {
        // given a valid artist id
        val validArtistId = "4zCH9qm4R2DADamUHMCa6O" // Anirudh Ravichander
        // when fetching the artist summary using the id
        val resource =
            artistsRepository.fetchArtistSummaryForId(validArtistId)
        // the return type must be of type FetchedResource.Success
        assert(resource is FetchedResource.Success)
    }

    @Test
    fun artistFetchTest_invalidArtistId_returnsFailedFetchedResource() = runBlocking {
        // given an invalid artist id
        val invalid = "-"
        // when fetching the artist summary using the id
        val resource =
            artistsRepository.fetchArtistSummaryForId(invalid)
        // the return type must be of type FetchedResource.Failure
        assert(resource is FetchedResource.Failure)
        // the error type must be MusifyHttpErrorType.INVALID_REQUEST
        assert((resource as FetchedResource.Failure).cause == MusifyErrorType.INVALID_REQUEST)
    }

}