package com.example.musify.viewmodels.homefeedviewmodel.greetingphrasegenerator

import java.time.LocalTime

/**
 * A concrete implementation of [GreetingPhraseGenerator] that generates
 * greeting phrases based on the provided [time] value. By default the
 * [time] property is set to [LocalTime.now].
 * @see CurrentTimeBasedGreetingPhraseGenerator
 */
class TimeBasedGreetingPhraseGenerator(
    private val time: LocalTime = LocalTime.now()
) : GreetingPhraseGenerator {
    override fun generatePhrase(): String = when {
        time.isMorning -> "Good morning"
        time.isNoon -> "Good afternoon"
        else -> "Good evening"
    }
}
