package com.example.musify.data.repositories.homefeedrepository

import com.example.musify.data.encoder.TestBase64Encoder
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.token.tokenmanager.TokenManager
import com.example.musify.data.repositories.tokenrepository.SpotifyTokenRepository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.utils.defaultMusifyJacksonConverterFactory
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class MusifyHomeFeedRepositoryTest {
    private lateinit var homeFeedRepository: HomeFeedRepository

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
        homeFeedRepository = MusifyHomeFeedRepository(
            tokenRepository = SpotifyTokenRepository(
                tokenManager,
                TestBase64Encoder()
            ),
            spotifyService = spotifyService
        )
    }

    @Test
    fun fetchNewReleasesTest_valid_country_code_returnsNonEmptyAlbumList() = runBlocking {
        val result = homeFeedRepository.fetchNewlyReleasedAlbums(
            countryCode = "US",
            mapperImageSize = MapperImageSize.MEDIUM
        )
        assert(result is FetchedResource.Success)
        assert((result as FetchedResource.Success).data.isNotEmpty())
    }

    @Test
    fun fetchFeaturedPlaylists_valid_timeStamp_and_country_code_returnsNonEmptyList() =
        runBlocking {
            val result = homeFeedRepository.fetchFeaturedPlaylistsForCurrentTimeStamp(
                timestampMillis = System.currentTimeMillis(),
                countryCode = "US",
                languageCode = ISO6391LanguageCode("en")
            )
            assert(result is FetchedResource.Success)
            assert((result as FetchedResource.Success).data.playlists.isNotEmpty())
        }

    @Test
    fun fetchPlaylistForCategoriesTest_valid_countryAndLangCode_returnNonEmptyPlaylistList() =
        runBlocking {
            val result = homeFeedRepository.fetchPlaylistsBasedOnCategoriesAvailableForCountry(
                countryCode = "US",
                languageCode = ISO6391LanguageCode("en")
            )
            assert(result is FetchedResource.Success)
            assert((result as FetchedResource.Success).data.isNotEmpty())
        }

}