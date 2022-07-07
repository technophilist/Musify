package com.example.musify.data.remote.token

import com.example.musify.BuildConfig
import com.example.musify.data.encoder.Base64Encoder

/**
 * A function that uses the [base64Encoder] to get an encoded
 * spotify client secret.
 */
fun getSpotifyClientSecret(base64Encoder: Base64Encoder): String {
    val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    val clientSecret = BuildConfig.SPOTIFY_CLIENT_SECRET
    return base64Encoder.encodeToString("Basic $clientId:$clientSecret".toByteArray())
}
