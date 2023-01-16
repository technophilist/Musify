package com.example.musify.viewmodels

import android.app.Application
import androidx.compose.runtime.derivedStateOf
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
import com.example.musify.ui.navigation.MusifyNavigationDestinations
import com.example.musify.usecases.getCurrentlyPlayingEpisodePlaybackStateUseCase.GetCurrentlyPlayingEpisodePlaybackStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.musify.usecases.getCurrentlyPlayingEpisodePlaybackStateUseCase.GetCurrentlyPlayingEpisodePlaybackStateUseCase.PlaybackState as UseCasePlaybackState

@HiltViewModel
class PodcastEpisodeDetailViewModel @Inject constructor(
    application: Application,
    private val podcastsRepository: PodcastsRepository,
    private val savedStateHandle: SavedStateHandle,
    getCurrentlyPlayingEpisodePlaybackStateUseCase: GetCurrentlyPlayingEpisodePlaybackStateUseCase,
) : AndroidViewModel(application) {

    enum class UiSate { IDLE, LOADING, PLAYBACK_LOADING, ERROR }

    private var currentlyPlayingEpisode by mutableStateOf<PodcastEpisode?>(null)

    var uiState by mutableStateOf(UiSate.IDLE)
        private set

    var podcastEpisode by mutableStateOf<PodcastEpisode?>(null)
        private set

    val isEpisodeCurrentlyPlaying by derivedStateOf { currentlyPlayingEpisode == podcastEpisode }

    init {
        fetchEpisodeUpdatingUiState()
        getCurrentlyPlayingEpisodePlaybackStateUseCase
            .currentlyPlayingEpisodePlaybackStateStream
            .onEach {
                when (it) {
                    is UseCasePlaybackState.Paused,
                    is UseCasePlaybackState.Ended -> currentlyPlayingEpisode = null
                    is UseCasePlaybackState.Loading -> uiState = UiSate.PLAYBACK_LOADING
                    is UseCasePlaybackState.Playing -> {
                        if (uiState != UiSate.IDLE) uiState = UiSate.IDLE
                        // Initially this.podcastEpisode might be null when the
                        // flow sends it's first emission. This makes it impossible
                        // to compare this.podcastEpisode and it.playingEpisode.
                        // Therefore, assign the property to a state variable.
                        currentlyPlayingEpisode = it.playingEpisode
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun fetchEpisodeUpdatingUiState() {
        viewModelScope.launch {
            uiState = UiSate.LOADING
            val episode = fetchEpisode()
            uiState = if (episode == null) {
                UiSate.ERROR
            } else {
                podcastEpisode = episode
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
