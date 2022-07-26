package com.example.musify.viewmodels.searchviewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musify.data.repository.Repository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.di.IODispatcher
import com.example.musify.di.MusifyApplication
import com.example.musify.domain.SearchResults
import com.example.musify.domain.emptySearchResults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * An enum class that contains the different ui states associated with
 * the [SearchViewModel].
 */
enum class SearchScreenUiState { LOADING, SUCCESS, IDLE }

@HiltViewModel
class SearchViewModel @Inject constructor(
    application: Application,
    private val repository: Repository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : AndroidViewModel(application) {
    private var searchJob: Job? = null
    private val emptySearchResults = emptySearchResults()
    private val _uiState = mutableStateOf(SearchScreenUiState.IDLE)
    private val _searchResults = mutableStateOf(emptySearchResults)
    private val filteredSearchResults = mutableStateOf(emptySearchResults)
    val searchResults = filteredSearchResults as State<SearchResults>
    val uiState = _uiState as State<SearchScreenUiState>

    // TODO test locale
    private fun getCountryCode(): String = getApplication<MusifyApplication>()
        .resources
        .configuration
        .locale
        .country

    private fun getSearchResultsObjectForFilter(searchFilter: SearchFilter) =
        if (searchFilter != SearchFilter.ALL) {
            SearchResults(
                tracks = if (searchFilter == SearchFilter.TRACKS) _searchResults.value.tracks
                else emptyList(),
                albums = if (searchFilter == SearchFilter.ALBUMS) _searchResults.value.albums
                else emptyList(),
                artists = if (searchFilter == SearchFilter.ARTISTS) _searchResults.value.artists
                else emptyList(),
                playlists = if (searchFilter == SearchFilter.PLAYLISTS) _searchResults.value.playlists
                else emptyList()
            )
        } else _searchResults.value

    fun searchWithFilter(
        searchQuery: String,
        searchFilter: SearchFilter = SearchFilter.ALL
    ) {
        searchJob?.cancel()
        if (searchQuery.isBlank()) {
            _searchResults.value = emptySearchResults
            filteredSearchResults.value = _searchResults.value
            _uiState.value = SearchScreenUiState.IDLE
            return
        }
        _uiState.value = SearchScreenUiState.LOADING
        searchJob = viewModelScope.launch(ioDispatcher) {
            // add artificial delay to limit the number of calls to
            // the api when the user is typing the search query.
            // adding this delay allows for a short window of time
            // which could be used to cancel this coroutine if the
            // search text is currently being typed; preventing
            // un-necessary calls to the api
            delay(500)
            val searchResult = repository
                .fetchSearchResultsForQuery(
                    searchQuery = searchQuery.trim(),
                    imageSize = MapperImageSize.MEDIUM,
                    countryCode = getCountryCode()
                )
            if (searchResult is FetchedResource.Success) {
                _searchResults.value = searchResult.data
                filteredSearchResults.value = getSearchResultsObjectForFilter(searchFilter)
            }
            _uiState.value = SearchScreenUiState.SUCCESS
        }
    }

    fun applyFilterToSearchResults(searchFilter: SearchFilter) {
        filteredSearchResults.value = getSearchResultsObjectForFilter(searchFilter)
    }

    fun getAvailableGenres() = repository.fetchAvailableGenres()
}