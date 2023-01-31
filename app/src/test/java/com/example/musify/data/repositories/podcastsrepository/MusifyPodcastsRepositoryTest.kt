package com.example.musify.data.repositories.podcastsrepository

import com.example.musify.data.encoder.TestBase64Encoder
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.token.tokenmanager.TokenManager
import com.example.musify.data.repositories.tokenrepository.SpotifyTokenRepository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.di.PagingConfigModule
import com.example.musify.utils.defaultMusifyJacksonConverterFactory
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class MusifyPodcastsRepositoryTest {
    private lateinit var podcastsRepository: PodcastsRepository

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
        podcastsRepository = MusifyPodcastsRepository(
            tokenRepository = SpotifyTokenRepository(
                tokenManager,
                TestBase64Encoder()
            ),
            spotifyService = spotifyService,
            pagingConfig = PagingConfigModule.provideDefaultPagingConfig()
        )
    }

    @Test
    fun fetchPodcastEpisodeTest_validEpisodeId_successfullyFetchesPodcastEpisode() = runBlocking {
        val validEpisodeId = "5pLYyCItRvIc2SEbuJ3eO8"
        val fetchedResource = podcastsRepository.fetchPodcastEpisode(
            episodeId = validEpisodeId,
            countryCode = "IN"
        )
        assert(fetchedResource is FetchedResource.Success)
    }

    @Test
    fun fetchPodcastShowTest_validPodcastShowId_successfullyFetchesShow() = runBlocking {
        val validShowId = "6o81QuW22s5m2nfcXWjucc"
        val fetchedResource = podcastsRepository.fetchPodcastShow(
            showId = validShowId,
            countryCode = "IN"
        )
        assert(fetchedResource is FetchedResource.Success)
    }
}