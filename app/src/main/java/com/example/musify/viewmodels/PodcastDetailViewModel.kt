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
import com.example.musify.domain.PodcastEpisode
import com.example.musify.musicplayer.MusicPlayerV2
import com.example.musify.ui.navigation.MusifyNavigationDestinations
import com.example.musify.usecases.getCurrentlyPlayingPodcastEpisodeUseCase.GetCurrentlyPlayingPodcastEpisodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastDetailViewModel @Inject constructor(
    application: Application,
    private val podcastsRepository: PodcastsRepository,
    private val savedStateHandle: SavedStateHandle,
    private val musicPlayerV2: MusicPlayerV2,
    getCurrentlyPlayingPodcastEpisodeUseCase: GetCurrentlyPlayingPodcastEpisodeUseCase
) : AndroidViewModel(application) {

    enum class UiSate { IDLE, LOADING, ERROR }

    private val _uiState = mutableStateOf(UiSate.IDLE)
    val uiState = _uiState as State<UiSate>
    private val _podcastEpisode = mutableStateOf<PodcastEpisode?>(null)
    val podcastEpisode = _podcastEpisode as State<PodcastEpisode?>

    private val _isPlaybackPaused = mutableStateOf<Boolean?>(null)
    val isPlaybackPaused = _isPlaybackPaused as State<Boolean?>

    val currentlyPlayingPodcastEpisode: Flow<PodcastEpisode?> =
        getCurrentlyPlayingPodcastEpisodeUseCase
            .getCurrentlyPlayingPodcastEpisodeStream()

    init {
        fetchEpisodeUpdatingUiState()
        musicPlayerV2.currentPlaybackStateStream
            .onEach {
                if (it is MusicPlayerV2.PlaybackState.Playing && _isPlaybackPaused.value == true) {
                    _isPlaybackPaused.value = false
                    return@onEach
                }
                if (it !is MusicPlayerV2.PlaybackState.Paused) return@onEach
                if (_isPlaybackPaused.value == true) return@onEach
                _isPlaybackPaused.value = true
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