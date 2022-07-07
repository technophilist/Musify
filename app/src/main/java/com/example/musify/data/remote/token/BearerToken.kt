package com.example.musify.data.remote.token

import java.time.LocalDateTime

/**
 * A class that contains an OAuth Bearer token. A 'Bearer' token always
 * starts with the word 'Bearer' followed by the api token.
 * Eg. 'Bearer <api token>'
 * This is the reason as to why the toString method is overridden to
 * include the prefix. This becomes especially useful when used in
 * conjunction with Retrofit, because, the toString method will be called
 * under the hood, when retrofit encounters this object. This will prevent
 * the boilerplate code of concatenating the token with the prefix
 * 'Bearer'.
 */
data class BearerToken(
    private val tokenString: String,
    val timeOfCreation: LocalDateTime,
    val secondsUntilExpiration: Int
) {
    val value get() = "Bearer $tokenString"
    override fun toString(): String = "Bearer $tokenString"
}

val BearerToken.isExpired: Boolean
    get() {
        val timeOfExpiration = timeOfCreation.plusSeconds(secondsUntilExpiration.toLong())
        return LocalDateTime.now() > timeOfExpiration
    }