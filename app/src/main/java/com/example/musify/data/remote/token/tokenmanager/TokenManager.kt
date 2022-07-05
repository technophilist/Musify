package com.example.musify.data.remote.token.tokenmanager

const val DEFAULT_GRANT_TYPE = "client_credentials"

interface TokenManager<TokenType> {
    suspend fun getAccessToken(
        grantType: String = DEFAULT_GRANT_TYPE,
        secret: String
    ): TokenType
}