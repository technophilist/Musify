package com.example.musify.viewmodels.homefeedviewmodel.greetingphrasegenerator

import org.junit.Test
import java.time.LocalTime

class TimeBasedGreetingPhraseGeneratorTest {

    @Test
    fun phraseGenerationTest_24hourIntRange_generatesValidPhrases() {
        (0..23).forEach { hour ->
            val phrase = TimeBasedGreetingPhraseGenerator(LocalTime.of(hour, 0)).generatePhrase()
            assert(
                phrase == when (hour) {
                    in 0..11 -> "Good morning"
                    in 12..17 -> "Good afternoon"
                    else -> "Good evening"
                }
            )
        }
    }
}