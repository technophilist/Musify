package com.example.musify.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musify.data.repository.MusifyRepository
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

@HiltViewModel
class SearchViewModel @Inject constructor(
    application: Application,
    private val repository: MusifyRepository,
    @IODispatcher private val defaultDispatcher: CoroutineDispatcher
) : AndroidViewModel(application) {
    private var searchJob: Job? = null
    private val emptySearchResults = emptySearchResults()
    private val _isLoading = mutableStateOf(false)
    private val _searchResults = mutableStateOf(emptySearchResults)
    val searchResults = _searchResults as State<SearchResults>
    val isLoading = _isLoading as State<Boolean>

    fun search(searchQuery: String) {
        _isLoading.value = true
        searchJob?.cancel()
        if (searchQuery.isBlank()) {
            _searchResults.value = emptySearchResults
            _isLoading.value = false
            return
        }
        // TODO Test locale
        val countryCode = getApplication<MusifyApplication>().resources.configuration.locale.country
        searchJob = viewModelScope.launch(defaultDispatcher) {
            delay(1_500)
            val searchResult = repository
                .fetchSearchResultsForQuery(
                    searchQuery = searchQuery.trim(),
                    imageSize = MapperImageSize.SMALL,
                    countryCode = countryCode
                )
            if (searchResult is FetchedResource.Success) _searchResults.value = searchResult.data
            _isLoading.value = false
        }
    }
}