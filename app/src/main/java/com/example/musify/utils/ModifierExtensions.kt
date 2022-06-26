package com.example.musify.utils

import androidx.compose.ui.Modifier

/**
 * An extension function that can be used to conditionally chain a
 * modifier.
 *
 * @param condition the condition based on which the modifier will be
 * chained/not chained.
 * @param block a lambda with a [Modifier] as receiver.
 */
fun Modifier.conditional(
    condition: Boolean,
    block: Modifier.() -> Modifier
): Modifier = if (condition) this.then(block()) else this