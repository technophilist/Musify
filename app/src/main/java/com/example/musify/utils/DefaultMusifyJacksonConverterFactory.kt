package com.example.musify.utils

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import retrofit2.converter.jackson.JacksonConverterFactory

/**
 * A [JacksonConverterFactory] that is configured to not throw an
 * exception when an unknown json property is encountered.
 */
val defaultMusifyJacksonConverterFactory: JacksonConverterFactory = JacksonConverterFactory.create(
    jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
)