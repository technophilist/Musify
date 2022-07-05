package com.example.musify.data.remote.token

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A DTO object that contains the [accessToken] for any specific api.
 * It also contains additional information such as the [secondsUntilExpiration]
 * and the [tokenType].
 */
data class AccessTokenResponseDTO(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("expires_in") val secondsUntilExpiration: Int,
    @JsonProperty("token_type") val tokenType: String
)