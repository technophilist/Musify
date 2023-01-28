package com.example.musify.viewmodels.searchviewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.musify.data.repositories.genresrepository.GenresRepository
import com.example.musify.data.repositories.searchrepository.SearchRepository
import com.example.musify.di.IODispatcher
import com.example.musify.domain.SearchResult
import com.example.musify.usecases.getCurrentlyPlayingTrackUseCase.GetCurrentlyPlayingTrackUseCase
import com.example.musify.usecases.getPlaybackLoadingStatusUseCase.GetPlaybackLoadingStatusUseCase
import com.example.musify.viewmodels.getCountryCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * An enum class that contains the different ui states associated with
 * the [SearchViewModel].
 */
enum class SearchScreenUiState { LOADING, IDLE }

@HiltViewModel
class SearchViewModel @Inject constructor(
    application: Application,
    getCurrentlyPlayingTrackUseCase: GetCurrentlyPlayingTrackUseCase,
    getPlaybackLoadingStatusUseCase: GetPlaybackLoadingStatusUseCase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val genresRepository: GenresRepository,
    private val searchRepository: SearchRepository
) : AndroidViewModel(application) {

    private var searchJob: Job? = null

    private val _uiState = mutableStateOf(SearchScreenUiState.IDLE)
    val uiState = _uiState as State<SearchScreenUiState>

    private val _currentlySelectedFilter = mutableStateOf(SearchFilter.TRACKS)
    val currentlySelectedFilter = _currentlySelectedFilter as State<SearchFilter>

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

    private val _podcastListForSearchQuery =
        MutableStateFlow<PagingData<SearchResult.PodcastSearchResult>>(PagingData.empty())
    val podcastListForSearchQuery =
        _podcastListForSearchQuery as Flow<PagingData<SearchResult.PodcastSearchResult>>

    private val _episodeListForSearchQuery =
        MutableStateFlow<PagingData<SearchResult.EpisodeSearchResult>>(PagingData.empty())

    val episodeListForSearchQuery =
        _episodeListForSearchQuery as Flow<PagingData<SearchResult.EpisodeSearchResult>>

    val currentlyPlayingTrackStream = getCurrentlyPlayingTrackUseCase.currentlyPlayingTrackStream

    init {
        getPlaybackLoadingStatusUseCase
            .loadingStatusStream
            .onEach { isPlaybackLoading ->
                if (isPlaybackLoading && _uiState.value != SearchScreenUiState.LOADING) {
                    _uiState.value = SearchScreenUiState.LOADING
                    return@onEach
                }
                if (!isPlaybackLoading && _uiState.value == SearchScreenUiState.LOADING) {
                    _uiState.value = SearchScreenUiState.IDLE
                    return@onEach
                }
            }
            .launchIn(viewModelScope)
    }

    private fun collectAndAssignSearchResults(searchQuery: String) {
        searchRepository.getPaginatedSearchStreamForAlbums(
            searchQuery = searchQuery,
            countryCode = getCountryCode()
        ).cachedIn(viewModelScope)
            .collectInViewModelScopeUpdatingUiState(currentlySelectedFilter.value == SearchFilter.ALBUMS) {
                _albumListForSearchQuery.value = it
            }
        searchRepository.getPaginatedSearchStreamForArtists(
            searchQuery = searchQuery,
            countryCode = getCountryCode()
        ).cachedIn(viewModelScope)
            .collectInViewModelScopeUpdatingUiState(currentlySelectedFilter.value == SearchFilter.ARTISTS) {
                _artistListForSearchQuery.value = it
            }
        searchRepository.getPaginatedSearchStreamForTracks(
            searchQuery = searchQuery,
            countryCode = getCountryCode()
        ).cachedIn(viewModelScope)
            .collectInViewModelScopeUpdatingUiState(currentlySelectedFilter.value == SearchFilter.TRACKS) {
                _trackListForSearchQuery.value = it
            }
        searchRepository.getPaginatedSearchStreamForPlaylists(
            searchQuery = searchQuery,
            countryCode = getCountryCode()
        ).cachedIn(viewModelScope)
            .collectInViewModelScopeUpdatingUiState(currentlySelectedFilter.value == SearchFilter.PLAYLISTS) {
                _playlistListForSearchQuery.value = it
            }
        searchRepository.getPaginatedSearchStreamForPodcasts(
            searchQuery = searchQuery,
            countryCode = getCountryCode()
        ).cachedIn(viewModelScope)
            .collectInViewModelScopeUpdatingUiState(currentlySelectedFilter.value == SearchFilter.PODCASTS) {
                _podcastListForSearchQuery.value = it
            }
        searchRepository.getPaginatedSearchStreamForEpisodes(
            searchQuery = searchQuery,
            countryCode = getCountryCode()
        ).cachedIn(viewModelScope)
            .collectInViewModelScopeUpdatingUiState(currentlySelectedFilter.value == SearchFilter.PODCASTS) {
                _episodeListForSearchQuery.value = it
            }

    }

    private fun setEmptyValuesToAllSearchResults() {
        _albumListForSearchQuery.value = PagingData.empty()
        _artistListForSearchQuery.value = PagingData.empty()
        _trackListForSearchQuery.value = PagingData.empty()
        _playlistListForSearchQuery.value = PagingData.empty()
        _podcastListForSearchQuery.value = PagingData.empty()
        _episodeListForSearchQuery.value = PagingData.empty()
    }

    /**
     * Used to collect a flow and set the the [_uiState] to
     * [SearchScreenUiState.SUCCESS] based on the [updateUiStatePredicate]
     * after running the [collectBlock]. The [_uiState] will be set to
     * [SearchScreenUiState.SUCCESS] if, and only if, [updateUiStatePredicate]
     * is true and the current value of [_uiState] is equal to
     * [SearchScreenUiState.LOADING].
     *
     * This method is mainly used to update the [_uiState] to
     * [SearchScreenUiState.SUCCESS] based on the [currentlySelectedFilter].
     * This prevents the [_uiState] to be assigned to [SearchScreenUiState.LOADING]
     * for as long as all the flows get collected. Instead, the ui state will be
     * updated to [SearchScreenUiState.SUCCESS] as soon as the flow
     * for the [currentlySelectedFilter] is collected.
     *
     * For eg: If the user has selected the current filter to be [SearchFilter.ALBUMS],
     * this [_uiState] will be set to [SearchScreenUiState.SUCCESS] immediately
     * after the search results for the album has been set. This precludes the
     * need for waiting for other flows, such as artists/tracks to be collected
     * before setting the [_uiState] to [SearchScreenUiState.SUCCESS]
     * for the search results since the user just wants to see the album list.
     */
    private fun <T> Flow<T>.collectInViewModelScopeUpdatingUiState(
        updateUiStatePredicate: Boolean,
        collectBlock: (T) -> Unit
    ) {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                collect {
                    collectBlock(it)
                    if (_uiState.value == SearchScreenUiState.LOADING && updateUiStatePredicate)
                        _uiState.value = SearchScreenUiState.IDLE
                }
            }
        }
    }

    fun search(searchQuery: String) {
        searchJob?.cancel()
        if (searchQuery.isBlank()) {
            setEmptyValuesToAllSearchResults()
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
            collectAndAssignSearchResults(searchQuery)
        }
    }

    fun getAvailableGenres() = genresRepository.fetchAvailableGenres()

    fun updateSearchFilter(newSearchFilter: SearchFilter) {
        _currentlySelectedFilter.value = newSearchFilter
    }
}