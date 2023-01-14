package com.example.musify.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.musify.data.repositories.podcastsrepository.PodcastsRepository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.PodcastEpisode
import com.example.musify.domain.PodcastShow
import com.example.musify.musicplayer.MusicPlayerV2
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
    musicPlayer: MusicPlayerV2,
    getCurrentlyPlayingStreamableUseCase: GetCurrentlyPlayingStreamableUseCase,
    getPlaybackLoadingStatusUseCase: GetPlaybackLoadingStatusUseCase,
    private val podcastsRepository: PodcastsRepository
) : AndroidViewModel(application) {

    enum class UiState { IDLE, LOADING, PLAYBACK_LOADING, ERROR }
    
    private val showId =
        savedStateHandle.get<String>(MusifyNavigationDestinations.PodcastShowDetailScreen.NAV_ARG_PODCAST_SHOW_ID)!!
    val episodesForShowStream = podcastsRepository.getPodcastEpisodesStreamForPodcastShow(
        showId = showId,
        countryCode = getCountryCode(),
        imageSize = MapperImageSize.MEDIUM
    )
    // TODO STOPSHIP when playback is done, the pause button is still
    // displayed in the ui
    val currentlyPlayingEpisode = getCurrentlyPlayingStreamableUseCase
        .currentlyPlayingStreamableStream
        .filterIsInstance<PodcastEpisode>()

    var uiState by mutableStateOf(UiState.IDLE)
        private set

    var podcastShow by mutableStateOf<PodcastShow?>(null)
        private set

    var isCurrentlyPlayingEpisodePaused by mutableStateOf<Boolean?>(null)
        private set

    init {
        fetchShowUpdatingUiState()
        getPlaybackLoadingStatusUseCase
            .loadingStatusStream
            .onEach { isPlaybackLoading ->
                if (isPlaybackLoading && uiState != UiState.PLAYBACK_LOADING) {
                    uiState = UiState.PLAYBACK_LOADING
                    return@onEach
                }
                if (uiState == UiState.PLAYBACK_LOADING) uiState = UiState.IDLE
            }.launchIn(viewModelScope)
        musicPlayer
            .currentPlaybackStateStream
            .onEach {
                if (it is MusicPlayerV2.PlaybackState.Playing && (isCurrentlyPlayingEpisodePaused == null || isCurrentlyPlayingEpisodePaused == true)) {
                    isCurrentlyPlayingEpisodePaused = false
                    return@onEach
                }
                if (it is MusicPlayerV2.PlaybackState.Paused && isCurrentlyPlayingEpisodePaused == false) {
                    isCurrentlyPlayingEpisodePaused = true
                }
            }.launchIn(viewModelScope)
    }

    fun retryFetchingShow() {
        fetchShowUpdatingUiState()
    }

    private fun fetchShowUpdatingUiState() {
        viewModelScope.launch {
            uiState = UiState.LOADING
            val result = podcastsRepository.fetchPodcastShow(
                showId = showId,
                countryCode = getCountryCode(),
                imageSize = MapperImageSize.LARGE
            )
            if (result is FetchedResource.Success) {
                uiState = UiState.IDLE
                podcastShow = result.data
            } else {
                uiState = UiState.ERROR
            }
        }
    }

}