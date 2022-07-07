package com.example.musify.data.remote.token

import org.junit.Test
import java.time.LocalDateTime

class TokenExpirationTest {
    private val expiredToken = BearerToken("", LocalDateTime.now(), 0)

    @Test
    fun tokenExpirationTest_expiredToken_expiredExtensionPropertyIsTrue() {
        // given an expired token
        // the 'isExpired' property must be true
        assert(expiredToken.isExpired)
    }

    @Test
    fun tokenExpirationTest_validToken_expiredExtensionPropertyIsFalse() {
        // given a valid token
        val validToken = expiredToken.copy(
            timeOfCreation = LocalDateTime.now(),
            secondsUntilExpiration = 3600
        )// expires in an hour
        // the 'isExpired' property must be false
        assert(!validToken.isExpired)
    }

    @Test
    fun tokenExpirationTest_tokenWhichExpiresInAnHour_isMarkedAsExpiredExactlyAfterAnHour() {
        // given a token which expires in an hour
        // when exactly one hour has passed
        val token = expiredToken.copy(
            timeOfCreation = LocalDateTime.now().minusSeconds(3600), // go back an hour
            secondsUntilExpiration = 3600 // expires in an hour
        )
        // then, the 'isExpired' property must be true
        assert(token.isExpired)
    }

    @Test
    fun tokenExpirationTest_tokenWhichExpiresInAnHour_isNotExpiredInThe59thMinute() {
        // given a token that is valid for an hour.
        //  when 59 minutes has elapsed
        val token = expiredToken.copy(
            timeOfCreation = LocalDateTime.now().minusSeconds(3599), // go back 59 minutes
            secondsUntilExpiration = 3600 // expires in an hour
        )
        // the 'isExpired' property must be false
        assert(!token.isExpired)
    }

    @Test
    fun tokenExpirationTest_tokenCreatedADayAgo_isMarkedAsExpired() {
        // given a token that was created a day ago
        // when 24 hours has elapsed
        val token = expiredToken.copy(
            timeOfCreation = LocalDateTime.now().minusDays(1),
            secondsUntilExpiration = 3600
        )
        // the 'isExpired' property must be true
        assert(token.isExpired)
    }
}