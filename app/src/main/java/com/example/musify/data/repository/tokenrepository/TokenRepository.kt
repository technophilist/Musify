package com.example.musify.data.repository.tokenrepository

import com.example.musify.data.remote.token.BearerToken

/**
 * An interface that contains all the methods that are requisite for
 * an class that implements [TokenRepository].
 */
interface TokenRepository {
    /**
     * Used to get an instance of [BearerToken].
     */
    suspend fun getBearerToken(): BearerToken
}g