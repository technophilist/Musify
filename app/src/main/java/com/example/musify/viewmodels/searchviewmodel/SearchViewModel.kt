package com.example.musify.viewmodels.searchviewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.musify.data.repository.Repository
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.di.MusifyApplication
import com.example.musify.domain.SearchResult
import com.example.musify.domain.SearchResults
import com.example.musify.domain.emptySearchResults
import com.example.musify.usecases.playtrackusecase.PlayTrackWithMediaNotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val playTrackWithMediaNotificationUseCase: PlayTrackWithMediaNotificationUseCase
) : AndroidViewModel(application) {
    private var searchJob: Job? = null
    private val emptySearchResults = emptySearchResults()
    private val _uiState = mutableStateOf(SearchScreenUiState.IDLE)
    private val _searchResults = mutableStateOf(emptySearchResults)
    private val filteredSearchResults = mutableStateOf(emptySearchResults)
    val uiState = _uiState as State<SearchScreenUiState>

    private val _albumListForSearchQuery =
        MutableStateFlow<PagingData<SearchResult.AlbumSearchResult>>(PagingData.empty())
    val albumListForSearchQuery =
        _albumListForSearchQuery as Flow<PagingData<SearchResult.AlbumSearchResult>>

    private val _artistListForSearchQuery =
        MutableStateFlow<PagingData<SearchResult.ArtistSearchResult>>(PagingData.empty())
    val artistListForSearchQuery =
        _artistListForSearchQuery as Flow<PagingData<SearchResult.ArtistSearchResult>>

    private val _trackListForSearchQuery =
        MutableStateFlow<PagingData<SearchResult.TrackSearchResult>>(PagingData.empty())
    val trackListForSearchQuery =
        _trackListForSearchQuery as Flow<PagingData<SearchResult.TrackSearchResult>>

    private val _playlistListForSearchQuery =
        MutableStateFlow<PagingData<SearchResult.PlaylistSearchResult>>(PagingData.empty())
    val playlistListForSearchQuery =
        _playlistListForSearchQuery as Flow<PagingData<SearchResult.PlaylistSearchResult>>

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

    private fun collectAndAssignSearchResults(
        searchQuery: String,
        imageSize: MapperImageSize
    ) {
        repository.getPaginatedSearchStreamForType(
            paginatedStreamType = Repository.PaginatedStreamType.ALBUMS,
            searchQuery = searchQuery,
            countryCode = getCountryCode(),
            imageSize = imageSize
        ).collectInViewModelScope {
            _albumListForSearchQuery.value = it as PagingData<SearchResult.AlbumSearchResult>
        }
        repository.getPaginatedSearchStreamForType(
            paginatedStreamType = Repository.PaginatedStreamType.ARTISTS,
            searchQuery = searchQuery,
            countryCode = getCountryCode(),
            imageSize = imageSize
        ).collectInViewModelScope {
            _artistListForSearchQuery.value = it as PagingData<SearchResult.ArtistSearchResult>
        }
        repository.getPaginatedSearchStreamForType(
            paginatedStreamType = Repository.PaginatedStreamType.TRACKS,
            searchQuery = searchQuery,
            countryCode = getCountryCode(),
            imageSize = imageSize
        ).collectInViewModelScope {
            _trackListForSearchQuery.value = it as PagingData<SearchResult.TrackSearchResult>
        }
        repository.getPaginatedSearchStreamForType(
            paginatedStreamType = Repository.PaginatedStreamType.PLAYLISTS,
            searchQuery = searchQuery,
            countryCode = getCountryCode(),
            imageSize = imageSize
        ).collectInViewModelScope {
            _playlistListForSearchQuery.value = it as PagingData<SearchResult.PlaylistSearchResult>
        }
    }

    private fun <T> Flow<T>.collectInViewModelScope(collectBlock: suspend (value: T) -> Unit) {
        viewModelScope.launch { collect { collectBlock(it) } }
    }

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
        searchJob = viewModelScope.launch {
            // add artificial delay to limit the number of calls to
            // the api when the user is typing the search query.
            // adding this delay allows for a short window of time
            // which could be used to cancel this coroutine if the
            // search text is currently being typed; preventing
            // un-necessary calls to the api
            delay(500)
            collectAndAssignSearchResults(searchQuery, MapperImageSize.MEDIUM)
            _uiState.value = SearchScreenUiState.SUCCESS // fixme
        }
    }

    fun applyFilterToSearchResults(searchFilter: SearchFilter) {
        filteredSearchResults.value = getSearchResultsObjectForFilter(searchFilter)
    }

    fun playTrack(track: SearchResult.TrackSearchResult) {
        if (track.trackUrlString == null) return
        viewModelScope.launch {
            playTrackWithMediaNotificationUseCase.invoke(
                track,
                onLoading = { _uiState.value = SearchScreenUiState.LOADING },
                onFinishedLoading = { _uiState.value = SearchScreenUiState.IDLE }
            )
        }
    }

    fun getAvailableGenres() = repository.fetchAvailableGenres()
}