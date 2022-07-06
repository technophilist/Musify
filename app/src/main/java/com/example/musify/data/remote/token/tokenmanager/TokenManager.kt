package com.example.musify.data.remote.token.tokenmanager

import android.util.Base64
import com.example.musify.BuildConfig
import com.example.musify.data.remote.musicservice.SpotifyEndPoints
import com.example.musify.data.remote.token.AccessTokenResponseDTO
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * A top level value that contains the client secret associated with
 * spotify's api in a Base64 encoded format. This string has to be
 * included in the header of an authorization request to get an
 * **access token** from the spotify api. This is **not** a token
 * than can be used to directly access spotify's api. It is secret
 * that can be sent to spotify's server's using the [TokenManager.getAccessToken]
 * to get the actual token that can be used to communicate with
 * spotify's servers.
 */
val SPOTIFY_CLIENT_SECRET_BASE64: String
    get() = Base64.encodeToString(
        "Basic ${BuildConfig.SPOTIFY_CLIENT_ID}:${BuildConfig.SPOTIFY_CLIENT_SECRET}".toByteArray(),
        Base64.NO_WRAP
    )

interface TokenManager {

    @FormUrlEncoded
    @POST(SpotifyEndPoints.API_TOKEN_ENDPOINT)
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String,
        @Header("Authorization") secret: String
    ): Response<AccessTokenResponseDTO>
}