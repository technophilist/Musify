package com.example.musify.data.repositories.tokenrepository

import com.example.musify.data.remote.token.BearerToken
import java.time.LocalDateTime

class TokenRepositoryStub : TokenRepository {
    override suspend fun getValidBearerToken() = BearerToken(
        "",
        LocalDateTime.now(),
        60
    )
}