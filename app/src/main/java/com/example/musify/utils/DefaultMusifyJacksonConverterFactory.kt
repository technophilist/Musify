package com.example.musify.utils

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import retrofit2.converter.jackson.JacksonConverterFactory

/**
 * A kotlin module with strict null checks enabled for collections.
 * TODO: consider using strict null checks only during the debug version and not for the release.
 */
private val jacksonModule = kotlinModule {
    configure(KotlinFeature.StrictNullChecks, true) // warning: this feature has a significant performance impact.
}

/**
 * A [JacksonConverterFactory] that is configured to not throw an
 * exception when an unknown json property is encountered.
 */
val defaultMusifyJacksonConverterFactory: JacksonConverterFactory = JacksonConverterFactory.create(
    jsonMapper { addModule(jacksonModule) }
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
)