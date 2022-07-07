package com.example.musify.data.repository.tokenrepository

import com.example.musify.data.encoder.Base64Encoder
import com.example.musify.data.remote.token.BearerToken
import com.example.musify.data.remote.token.getSpotifyClientSecret
import com.example.musify.data.remote.token.isExpired
import com.example.musify.data.remote.token.toBearerToken
import com.example.musify.data.remote.token.tokenmanager.TokenManager

class SpotifyTokenRepository(
    private val tokenManager: TokenManager,
    private val base64Encoder: Base64Encoder
) : TokenRepository {
    private var token: BearerToken? = null
    override suspend fun getBearerToken(): BearerToken {
        if (token == null || token?.isExpired == true) getAndAssignToken()
        return token!!
    }

    /**
     * A helper function that gets and assigns a new [token].
     */
    private suspend fun getAndAssignToken() {
        val clientSecret = getSpotifyClientSecret(base64Encoder)
        token = tokenManager
            .getNewAccessToken(clientSecret)
            .toBearerToken()
    }
}