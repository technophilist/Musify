package com.example.musify.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.musify.data.repositories.tracksrepository.TracksRepository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.SearchResult
import com.example.musify.ui.navigation.MusifyNavigationDestinations
import com.example.musify.usecases.getCurrentlyPlayingTrackUseCase.GetCurrentlyPlayingTrackUseCase
import com.example.musify.usecases.getPlaybackLoadingStatusUseCase.GetPlaybackLoadingStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AlbumDetailUiState {
    object Idle : AlbumDetailUiState()
    object Loading : AlbumDetailUiState()
    data class Error(private val message: String) : AlbumDetailUiState()
}

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    getCurrentlyPlayingTrackUseCase: GetCurrentlyPlayingTrackUseCase,
    getPlaybackLoadingStatusUseCase: GetPlaybackLoadingStatusUseCase,
    private val tracksRepository: TracksRepository,
) : AndroidViewModel(application) {


    private val _tracks = mutableStateOf<List<SearchResult.TrackSearchResult>>(emptyList())
    val tracks = _tracks as State<List<SearchResult.TrackSearchResult>>

    private val _uiState = mutableStateOf<AlbumDetailUiState>(AlbumDetailUiState.Idle)
    val uiState = _uiState as State<AlbumDetailUiState>

    private val albumId =
        savedStateHandle.get<String>(MusifyNavigationDestinations.AlbumDetailScreen.NAV_ARG_ALBUM_ID)!!
    private val defaultMapperImageSize = MapperImageSize.MEDIUM
    val currentlyPlayingTrackStream =
        getCurrentlyPlayingTrackUseCase.getCurrentlyPlayingTrackStream()

    init {
        fetchAndAssignTrackList()
        getPlaybackLoadingStatusUseCase
            .loadingStatusStream
            .onEach { isPlaybackLoading ->
                if (isPlaybackLoading && _uiState.value !is AlbumDetailUiState.Loading) {
                    _uiState.value = AlbumDetailUiState.Loading
                    return@onEach
                }
                if (!isPlaybackLoading && _uiState.value is AlbumDetailUiState.Loading) {
                    _uiState.value = AlbumDetailUiState.Idle
                    return@onEach
                }
            }
    }

    private fun fetchAndAssignTrackList() {
        viewModelScope.launch {
            _uiState.value = AlbumDetailUiState.Loading
            val result = tracksRepository.fetchTracksForAlbumWithId(
                albumId = albumId,
                countryCode = getCountryCode(),
                imageSize = defaultMapperImageSize
            )
            if (result is FetchedResource.Success) {
                _tracks.value = result.data
                _uiState.value = AlbumDetailUiState.Idle
            } else {
                _uiState.value =
                    AlbumDetailUiState.Error("Unable to fetch tracks. Please check internet connection.")
            }
        }
    }

}