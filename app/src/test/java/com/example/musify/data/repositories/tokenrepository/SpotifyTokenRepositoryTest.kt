package com.example.musify.data.repositories.tokenrepository

import com.example.musify.data.encoder.TestBase64Encoder
import com.example.musify.data.remote.token.AccessTokenResponse
import com.example.musify.data.remote.token.isExpired
import com.example.musify.data.remote.token.tokenmanager.TokenManager
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SpotifyTokenRepositoryTest {
    private lateinit var tokenRepository: TokenRepository

    @Before
    fun setUp() {
        val tokenManagerMock = object : TokenManager {
            private var numberOfCallsToGetNewAccessToken = 0

            // Returns an expired token the first time this method is called.
            // Returns a valid token the second time this method is called.
            // This is an unrealistic mock because, a request to a
            // new access token can never result in getting an expired token.
            override suspend fun getNewAccessToken(
                secret: String,
                grantType: String
            ): AccessTokenResponse = AccessTokenResponse(
                accessToken = "Fake Token",
                secondsUntilExpiration = if (++numberOfCallsToGetNewAccessToken == 1) 0
                else 3600,
                tokenType = "Fake Token"
            )
        }
        tokenRepository = SpotifyTokenRepository(tokenManagerMock, TestBase64Encoder())
    }

    @Test
    fun getTokenTest_repositoryContainsExpiredTokenWhenRequesting_automaticallyGetsAndReturnsNewToken() =
        runBlocking {
            // Initially the token repository would not have any token.
            // Therefore, requesting a token would result in getting a
            // new expired token (because of the mock).

            // Given a token repository with expired token.
            assert(tokenRepository.getValidBearerToken().isExpired)

            // The repository would now have an expired token.
            // Since the previous token was expired, it should automatically
            // get a new token and return that, instead of returning
            // the expired token when getBearerToken() is called.

            // when getBearerToken() is called
            // the token must not not be expired.
            assert(tokenRepository.getValidBearerToken().isExpired)
        }


}