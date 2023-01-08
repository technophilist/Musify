package com.example.musify.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.musify.data.repositories.podcastsrepository.PodcastsRepository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.PodcastShow
import com.example.musify.domain.SearchResult
import com.example.musify.ui.navigation.MusifyNavigationDestinations
import com.example.musify.usecases.getCurrentlyPlayingStreamableUseCase.GetCurrentlyPlayingStreamableUseCase
import com.example.musify.usecases.getPlaybackLoadingStatusUseCase.GetPlaybackLoadingStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastShowDetailViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    getCurrentlyPlayingStreamableUseCase: GetCurrentlyPlayingStreamableUseCase,
    getPlaybackLoadingStatusUseCase: GetPlaybackLoadingStatusUseCase,
    private val podcastsRepository: PodcastsRepository
) : AndroidViewModel(application) {

    enum class UiState { IDLE, LOADING, PLAYBACK_LOADING, ERROR }

    private val _uiState = mutableStateOf(UiState.IDLE)
    val uiState = _uiState as State<UiState>
    private val showId =
        savedStateHandle.get<String>(MusifyNavigationDestinations.PodcastShowDetailScreen.NAV_ARG_PODCAST_SHOW_ID)!!
    val episodesForShowStream = podcastsRepository.getEpisodesStreamForPodcastShow(
        showId = showId,
        countryCode = getCountryCode(),
        imageSize = MapperImageSize.MEDIUM
    )

    private val _podcastShow = mutableStateOf<PodcastShow?>(null)
    val podcastShow = _podcastShow as State<PodcastShow?>

    val currentlyPlayingEpisode = getCurrentlyPlayingStreamableUseCase
        .currentlyPlayingStreamableFlow
        .filterIsInstance<SearchResult.StreamableEpisodeSearchResult>()

    init {
        fetchShowUpdatingUiState()
        getPlaybackLoadingStatusUseCase
            .loadingStatusStream
            .onEach { isPlaybackLoading ->
                if (isPlaybackLoading && _uiState.value != UiState.PLAYBACK_LOADING) {
                    _uiState.value = UiState.PLAYBACK_LOADING
                    return@onEach
                }
                if (_uiState.value == UiState.PLAYBACK_LOADING) _uiState.value = UiState.IDLE
            }.launchIn(viewModelScope)
    }

    fun retryFetchingShow() {
        fetchShowUpdatingUiState()
    }

    private fun fetchShowUpdatingUiState() {
        viewModelScope.launch {
            _uiState.value = UiState.LOADING
            val result = podcastsRepository.fetchPodcastShow(
                showId = showId,
                countryCode = getCountryCode(),
                imageSize = MapperImageSize.LARGE
            )
            if (result is FetchedResource.Success) {
                _uiState.value = UiState.IDLE
                _podcastShow.value = result.data
            } else {
                _uiState.value = UiState.ERROR
            }
        }
    }

}