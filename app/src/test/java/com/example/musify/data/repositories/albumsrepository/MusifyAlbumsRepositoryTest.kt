package com.example.musify.data.repositories.albumsrepository

import com.example.musify.data.encoder.TestBase64Encoder
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.token.tokenmanager.TokenManager
import com.example.musify.data.repositories.tokenrepository.SpotifyTokenRepository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.di.PagingConfigModule
import com.example.musify.domain.MusifyErrorType
import com.example.musify.utils.defaultMusifyJacksonConverterFactory
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class MusifyAlbumsRepositoryTest {

    private lateinit var musifyAlbumsRepository: MusifyAlbumsRepository

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
        musifyAlbumsRepository = MusifyAlbumsRepository(
            tokenRepository = SpotifyTokenRepository(
                tokenManager,
                TestBase64Encoder()
            ),
            spotifyService = spotifyService,
            pagingConfig = PagingConfigModule.provideDefaultPagingConfig()
        )
    }


    @Test
    fun fetchArtistAlbumsTest_validArtistIdAndCountryCode_isFetchedSuccessfully() {
        // given a valid artist id
        val validArtistId = "4zCH9qm4R2DADamUHMCa6O" // Anirudh Ravichander
        val countryCode = "IN"
        // when fetching the albums
        val result = runBlocking {
            musifyAlbumsRepository.fetchAlbumsOfArtistWithId(
                validArtistId,
                MapperImageSize.SMALL,
                countryCode
            )
        }
        // the return type must be of type FetchedResource.Success
        assert(result is FetchedResource.Success)
        // the list should not be empty
        assert((result as FetchedResource.Success).data.isNotEmpty())
    }

    @Test
    fun fetchArtistAlbumsTest_validArtistIdAndInvalidCountryCode_returnsFailedFetchedResource() {
        // given a valid artist id
        val validArtistId = "4zCH9qm4R2DADamUHMCa6O" // Anirudh Ravichander
        // and an invalid country code
        val countryCode = "0"
        // when fetching the albums
        val result = runBlocking {
            musifyAlbumsRepository.fetchAlbumsOfArtistWithId(
                validArtistId,
                MapperImageSize.SMALL,
                countryCode
            )
        }
        // the return type must be of type FetchedResource.Failure
        assert(result is FetchedResource.Failure)
        assert((result as FetchedResource.Failure).cause == MusifyErrorType.INVALID_REQUEST)
    }


    @Test
    fun fetchAlbumTest_validAlbumId_isSuccessfullyFetched() {
        // given an valid albumId
        val albumId = "4aawyAB9vmqN3uQ7FjRGTy"
        // when fetching the album
        val result = runBlocking {
            musifyAlbumsRepository.fetchAlbumWithId(albumId, MapperImageSize.SMALL, "IN")
        }
        // the return type must be of type FetchedResource.Success
        assert(result is FetchedResource.Success)
    }
}