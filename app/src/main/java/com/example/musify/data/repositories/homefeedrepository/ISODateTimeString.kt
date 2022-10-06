package com.example.musify.data.repositories.homefeedrepository

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * A helper class that is meant to contain methods used for converting
 * a timestamp to a string that conforms to ISO-8601 standard.
 */
class ISODateTimeString {
    companion object {
        /**
         * Used to construct a date time string from the provided [millis]
         * that conforms to the ISO 8601 format.
         */
        fun from(millis: Long): String = LocalDateTime
            .ofInstant(
                Instant.ofEpochMilli(millis),
                ZoneId.systemDefault()
            )
            .truncatedTo(ChronoUnit.SECONDS)
            .format(DateTimeFormatter.ISO_DATE_TIME)
    }
}
