package com.example.musify.data.repositories.homefeedrepository

import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class ISODateTimeStringTest {

    @Test
    fun timeStringConversionTest_validMillis_getsSuccessfullyConverted() {
        val millis = LocalDateTime
            .of(
                LocalDate.of(2022, 12, 12),
                LocalTime.of(12, 12, 12)
            )
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        assert(ISODateTimeString.from(millis) == "2022-12-12T12:12:12")
    }

    @Test
    fun timeStringConversionTest_singleDigitValues_getsPaddedWithLeadingZeros() {
        val millis = LocalDateTime
            .of(
                LocalDate.of(2022, 7, 7),
                LocalTime.of(7, 7, 7)
            )
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        assert(ISODateTimeString.from(millis) == "2022-07-07T07:07:07")
    }

}