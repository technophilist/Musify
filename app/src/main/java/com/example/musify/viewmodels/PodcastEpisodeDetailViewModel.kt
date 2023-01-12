package com.example.musify.viewmodels

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.musify.data.repositories.podcastsrepository.PodcastsRepository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.PodcastEpisode
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
class PodcastEpisodeDetailViewModel @Inject constructor(
    application: Application,
    private val podcastsRepository: PodcastsRepository,
    private val savedStateHandle: SavedStateHandle,
    musicPlayerV2: MusicPlayerV2,
    getCurrentlyPlayingStreamableUseCase: GetCurrentlyPlayingStreamableUseCase,
    getPlaybackLoadingStatusUseCase: GetPlaybackLoadingStatusUseCase
) : AndroidViewModel(application) {

    enum class UiSate { IDLE, LOADING, PLAYBACK_LOADING, ERROR }

    private val _uiState = mutableStateOf(UiSate.IDLE)
    val uiState = _uiState as State<UiSate>
    private val _podcastEpisode = mutableStateOf<PodcastEpisode?>(null)
    val podcastEpisode = _podcastEpisode as State<PodcastEpisode?>

    private var isMusicPlayerPlaying by mutableStateOf<Boolean?>(null)
    private var currentlyPlayingPodcastEpisode by mutableStateOf<PodcastEpisode?>(null)

    val isEpisodeCurrentlyPlaying = derivedStateOf {
        isMusicPlayerPlaying == true && currentlyPlayingPodcastEpisode == podcastEpisode.value
    }

    init {
        fetchEpisodeUpdatingUiState()
        
        getPlaybackLoadingStatusUseCase
            .loadingStatusStream
            .onEach { isPlaybackLoading ->
                if (isPlaybackLoading && _uiState.value != UiSate.PLAYBACK_LOADING) {
                    _uiState.value = UiSate.PLAYBACK_LOADING
                    return@onEach
                }
                if (!isPlaybackLoading && _uiState.value == UiSate.PLAYBACK_LOADING) {
                    _uiState.value = UiSate.IDLE
                    return@onEach
                }
            }
            .launchIn(viewModelScope)

        getCurrentlyPlayingStreamableUseCase
            .currentlyPlayingStreamableStream
            .filterIsInstance<PodcastEpisode>()
            .onEach { currentlyPlayingPodcastEpisode = it }
            .launchIn(viewModelScope)
        musicPlayerV2.currentPlaybackStateStream
            .onEach {
                if (it is MusicPlayerV2.PlaybackState.Playing && (isMusicPlayerPlaying == false || isMusicPlayerPlaying == null)) {
                    isMusicPlayerPlaying = true
                    return@onEach
                }
                if (it is MusicPlayerV2.PlaybackState.Ended || it is MusicPlayerV2.PlaybackState.Paused) {
                    if (isMusicPlayerPlaying == true) isMusicPlayerPlaying = false
                }
            }
            .launchIn(viewModelScope)
    }

    private fun fetchEpisodeUpdatingUiState() {
        viewModelScope.launch {
            _uiState.value = UiSate.LOADING
            val episode = fetchEpisode()
            _uiState.value = if (episode == null) {
                UiSate.ERROR
            } else {
                _podcastEpisode.value = episode
                UiSate.IDLE
            }
        }
    }

    private suspend fun fetchEpisode(): PodcastEpisode? {
        val fetchedResource = podcastsRepository.fetchPodcastEpisode(
            episodeId = savedStateHandle[MusifyNavigationDestinations.PodcastEpisodeDetailScreen.NAV_ARG_PODCAST_EPISODE_ID]!!,
            countryCode = getCountryCode(),
            imageSize = MapperImageSize.LARGE // image would be used for both the mini player and the full screen player
        )
        return if (fetchedResource is FetchedResource.Success) fetchedResource.data else null
    }

    fun retryFetchingEpisode() {
        fetchEpisodeUpdatingUiState()
    }
}