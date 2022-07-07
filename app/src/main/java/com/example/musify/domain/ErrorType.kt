package com.example.musify.domain

import retrofit2.HttpException

/**
 * An enum that contains different error types associated with [HttpException.code].
 */
enum class MusifyHttpErrorType { BAD_OR_EXPIRED_TOKEN, BAD_OAUTH_REQUEST, RATE_LIMIT_EXCEEDED, UNKNOWN_ERROR }

/**
 * An extension property on [retrofit2.HttpException] that indicates
 * the [MusifyHttpErrorType] associated with the [retrofit2.HttpException.code]
 */
val HttpException.musifyHttpErrorType: MusifyHttpErrorType
    get() =
        when (this.code()) {
            401 -> MusifyHttpErrorType.BAD_OR_EXPIRED_TOKEN
            403 -> MusifyHttpErrorType.BAD_OAUTH_REQUEST
            429 -> MusifyHttpErrorType.RATE_LIMIT_EXCEEDED
            else -> MusifyHttpErrorType.UNKNOWN_ERROR
        }