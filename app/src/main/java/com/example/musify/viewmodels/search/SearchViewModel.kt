package com.example.musify.viewmodels.search

import androidx.compose.runtime.State
import com.example.musify.domain.SearchResults

/**
 * An interface that contains the requisite properties and methods
 * for a concrete implementation of [SearchViewModel].
 */
interface SearchViewModel {
    val searchResults: State<SearchResults>
    fun search(searchQuery: String)
}