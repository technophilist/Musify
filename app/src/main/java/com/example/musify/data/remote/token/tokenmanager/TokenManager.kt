package com.example.musify.data.remote.token.tokenmanager

import com.example.musify.data.remote.musicservice.SpotifyEndPoints
import com.example.musify.data.remote.token.AccessTokenResponseDTO
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

const val defaultGrantType = "client_credentials"

interface TokenManager {

    @FormUrlEncoded
    @POST(SpotifyEndPoints.API_TOKEN_ENDPOINT)
    suspend fun getNewAccessToken(
        @Header("Authorization") secret: String,
        @Field("grant_type") grantType: String = defaultGrantType,
    ): AccessTokenResponseDTO
}