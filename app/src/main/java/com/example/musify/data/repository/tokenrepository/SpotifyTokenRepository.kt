package com.example.musify.data.repository.tokenrepository

import com.example.musify.data.remote.token.BearerToken
import com.example.musify.data.remote.token.isExpired
import com.example.musify.data.remote.token.toBearerToken
import com.example.musify.data.remote.token.tokenmanager.SPOTIFY_CLIENT_SECRET_BASE64
import com.example.musify.data.remote.token.tokenmanager.TokenManager

class SpotifyTokenRepository(private val tokenManager: TokenManager) : TokenRepository {
    private var token: BearerToken? = null
    override suspend fun getBearerToken(): BearerToken {
        if (token == null || token?.isExpired == true) getAndAssignToken()
        return token!!
    }

    /**
     * A helper function that gets and assigns a new token to [token]
     * using the [clientSecret].
     */
    private suspend fun getAndAssignToken(clientSecret: String = SPOTIFY_CLIENT_SECRET_BASE64) {
        token = tokenManager
            .getNewAccessToken(clientSecret)
            .toBearerToken()
    }
}