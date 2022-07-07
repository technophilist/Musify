package com.example.musify.data.remote.token.tokenmanager


import com.example.musify.data.encoder.Base64Encoder
import com.example.musify.data.encoder.TestBase64Encoder
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.token.getSpotifyClientSecret
import com.example.musify.data.remote.token.toBearerToken
import com.example.musify.utils.defaultMusifyJacksonConverterFactory
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class TokenManagerTest {
    // Anirudh Ravichander
    private val validArtistId = "4zCH9qm4R2DADamUHMCa6O"
    private lateinit var tokenManager: TokenManager
    private lateinit var testBase64Encoder: Base64Encoder

    @Before
    fun setup() {
        tokenManager = Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/")
            .addConverterFactory(defaultMusifyJacksonConverterFactory)
            .build()
            .create(TokenManager::class.java)
        testBase64Encoder = TestBase64Encoder()
    }

    @Test
    fun getAccessTokenTest_validClientSecret_returnsAccessToken() {
        // given a valid client secret
        val clientSecret = getSpotifyClientSecret(testBase64Encoder)
        // the access token must be fetched without an exceptions
        runBlocking { tokenManager.getNewAccessToken(clientSecret) }
    }

    @Test
    fun useAccessTokenTest_newAccessToken_isAbleToSuccessfullyMakeRequest() {
        // given a valid client secret
        val clientSecret = getSpotifyClientSecret(testBase64Encoder)
        // when requesting an access token
        val accessTokenResponse = runBlocking {
            // the access token must be fetched without an exceptions
            tokenManager.getNewAccessToken(clientSecret)
        }
        // when using the newly acquired access token to get an artist
        val spotifyService = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(defaultMusifyJacksonConverterFactory)
            .build()
            .create(SpotifyService::class.java)
        // the artist must be fetched successfully
        runBlocking {
            spotifyService.getArtistInfoWithId(
                validArtistId,
                accessTokenResponse.toBearerToken()
            )
        }
    }
}