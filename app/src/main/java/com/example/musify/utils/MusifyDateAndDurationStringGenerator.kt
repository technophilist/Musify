package com.example.musify.utils

import android.content.Context
import com.example.musify.R

/**
 * A utility function used to generate a string in the following format:
 * "[month] [day], [year] • [hours] hr/hrs [minutes] min/mins". Here are a
 * few examples of how the string will be generated.
 *
 * if a short month name is passed, the string will be generated
 * in the following manner : "Jan 10, 2022 • 1 hr 10 mins"
 *
 * If the [month] is passed as fully qualified name, then it will be generated
 * in the following manner : "January 10, 2022 • 1 hr 10 mins"
 */
fun generateMusifyDateAndDurationString(
    context: Context,
    month: String,
    day: Int,
    year: Int,
    hours: Int,
    minutes: Int
): String {
    val dateString = "$month $day, $year"
    val hourString = if (hours == 0) ""
    else context.getQuantityStringResource(
        id = R.plurals.numberOfHoursOfEpisode,
        quantity = hours,
        formatArgs = arrayOf(hours)
    )
    val minuteString = context.getQuantityStringResource(
        id = R.plurals.numberOfHoursOfEpisode,
        quantity = minutes,
        formatArgs = arrayOf(minutes)
    )
    return "$dateString • $hourString $minuteString"
}

/**
 * Since [Context.getResources] is a java method, it cannot used named args.
 * This extension function is a wrapper, that just uses [Context.getResources]
 * to get a quantity string. Since this function is a kotlin function, the
 * allows the caller to use named arguments.
 */
private fun Context.getQuantityStringResource(
    id: Int,
    quantity: Int,
    vararg formatArgs: Any? = emptyArray()
) = resources.getQuantityString(id, quantity, formatArgs)