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
import com.example.musify.ui.navigation.MusifyNavigationDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastShowDetailViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val podcastsRepository: PodcastsRepository
) : AndroidViewModel(application) {

    enum class UiState { IDLE, LOADING, ERROR }

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

    init {
        fetchShowUpdatingUiState()
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