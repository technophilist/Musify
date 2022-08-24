package com.example.musify.viewmodels

import androidx.lifecycle.AndroidViewModel
import com.example.musify.di.MusifyApplication

/**
 * Used to get the country/region code for this locale, which should
 * either be the empty string, an uppercase ISO 3166 2-letter code,
 * or a UN M.49 3-digit code.
 *
 * @return The country/region code, or the empty string if none is defined.
 */
fun AndroidViewModel.getCountryCode(): String = this.getApplication<MusifyApplication>()
    .resources
    .configuration
    .locale
    .country