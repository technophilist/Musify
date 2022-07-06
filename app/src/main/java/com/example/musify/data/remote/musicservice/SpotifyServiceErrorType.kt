package com.example.musify.data.remote.musicservice

import retrofit2.HttpException

/**
 * An enum class that contains all the error types associated with the
 * Spotify API.
 */
enum class SpotifyServiceErrorType { BAD_OR_EXPIRED_TOKEN, BAD_OAUTH_REQUEST, RATE_LIMIT_EXCEEDED, UNKNOWN_ERROR }

/**
 * An extension property on [retrofit2.HttpException] that indicates
 * the [spotifyApiErrorType] associated with the [retrofit2.HttpException.code]
 */
val HttpException.spotifyApiErrorType: SpotifyServiceErrorType
    get() =
        when (this.code()) {
            401 -> SpotifyServiceErrorType.BAD_OR_EXPIRED_TOKEN
            403 -> SpotifyServiceErrorType.BAD_OAUTH_REQUEST
            429 -> SpotifyServiceErrorType.RATE_LIMIT_EXCEEDED
            else -> SpotifyServiceErrorType.UNKNOWN_ERROR
        }