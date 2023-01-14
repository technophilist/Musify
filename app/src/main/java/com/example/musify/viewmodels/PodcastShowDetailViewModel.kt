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
import com.example.musify.ui.navigation.MusifyNavigationDestinations
import com.example.musify.usecases.getCurrentlyPlayingEpisodePlaybackStateUseCase.GetCurrentlyPlayingEpisodePlaybackStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.musify.usecases.getCurrentlyPlayingEpisodePlaybackStateUseCase.GetCurrentlyPlayingEpisodePlaybackStateUseCase.PlaybackState as UseCasePlaybackState


@HiltViewModel
class PodcastShowDetailViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    getCurrentlyPlayingEpisodePlaybackStateUseCase: GetCurrentlyPlayingEpisodePlaybackStateUseCase,
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

    var currentlyPlayingEpisode by mutableStateOf<PodcastEpisode?>(null)
        private set

    var uiState by mutableStateOf(UiState.IDLE)
        private set

    var podcastShow by mutableStateOf<PodcastShow?>(null)
        private set

    var isCurrentlyPlayingEpisodePaused by mutableStateOf<Boolean?>(null)
        private set

    init {
        fetchShowUpdatingUiState()
        getCurrentlyPlayingEpisodePlaybackStateUseCase
            .currentlyPlayingEpisodePlaybackStateStream
            .onEach {
                when (it) {
                    is UseCasePlaybackState.Ended ->{
                        if (isCurrentlyPlayingEpisodePaused == false) {
                            isCurrentlyPlayingEpisodePaused = true
                        }
                        currentlyPlayingEpisode = null
                    }
                    is UseCasePlaybackState.Loading -> uiState = UiState.PLAYBACK_LOADING
                    is UseCasePlaybackState.Paused -> isCurrentlyPlayingEpisodePaused = true
                    is UseCasePlaybackState.Playing -> {
                        if (uiState != UiState.IDLE) uiState = UiState.IDLE
                        if (isCurrentlyPlayingEpisodePaused == null || isCurrentlyPlayingEpisodePaused == true) {
                            isCurrentlyPlayingEpisodePaused = false
                        }
                        currentlyPlayingEpisode = it.playingEpisode
                    }
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