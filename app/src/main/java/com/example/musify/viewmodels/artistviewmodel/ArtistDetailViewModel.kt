package com.example.musify.viewmodels.artistviewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.musify.data.repository.MusifyRepository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.di.MusifyApplication
import com.example.musify.domain.SearchResult
import com.example.musify.ui.navigation.MusifyNavigationDestinations
import com.example.musify.usecases.playtrackusecase.PlayTrackWithMediaNotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A sealed class hierarchy consisting of all UI states that are related to a screen
 * displaying the details of an artist.
 */
sealed class ArtistDetailScreenUiState {
    object Idle : ArtistDetailScreenUiState()
    object Loading : ArtistDetailScreenUiState()
    data class PlayingTrack(
        val currentlyPlayingTrack: SearchResult.TrackSearchResult
    ) : ArtistDetailScreenUiState()

    data class Error(private val message: String) : ArtistDetailScreenUiState()
}

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val repository: MusifyRepository,
    private val playTrackWithMediaNotificationUseCase: PlayTrackWithMediaNotificationUseCase,
) : AndroidViewModel(application) {

    private val _popularTracks = mutableStateOf<List<SearchResult.TrackSearchResult>>(emptyList())
    val popularTracks = _popularTracks as State<List<SearchResult.TrackSearchResult>>

    private val _uiState = mutableStateOf<ArtistDetailScreenUiState>(ArtistDetailScreenUiState.Idle)
    val uiState = _uiState as State<ArtistDetailScreenUiState>

    private val defaultMapperImageSize = MapperImageSize.MEDIUM
    private val artistId =
        savedStateHandle.get<String>(MusifyNavigationDestinations.ArtistDetailScreen.NAV_ARG_ARTIST_ID)!!


    val albumsOfArtistFlow = repository.getPaginatedStreamForAlbumsOfArtist(
        artistId = artistId,
        countryCode = getCountryCode(),
        imageSize = defaultMapperImageSize
    ).cachedIn(viewModelScope)

    init {
        viewModelScope.launch { fetchAndAssignPopularTracks() }
    }

    // TODO test locale
    private fun getCountryCode(): String = getApplication<MusifyApplication>()
        .resources
        .configuration
        .locale
        .country

    private suspend fun fetchAndAssignPopularTracks() {
        _uiState.value = ArtistDetailScreenUiState.Loading
        val fetchResult = repository.fetchTopTenTracksForArtistWithId(
            artistId = artistId,
            imageSize = defaultMapperImageSize,
            countryCode = getCountryCode()
        )
        when (fetchResult) {
            is FetchedResource.Failure -> {
                _uiState.value =
                    ArtistDetailScreenUiState.Error("Error loading tracks, please check internet connection")
            }
            is FetchedResource.Success -> {
                _popularTracks.value = fetchResult.data
                _uiState.value = ArtistDetailScreenUiState.Idle
            }
        }
    }

    fun playTrack(track: SearchResult.TrackSearchResult) {
        if (track.trackUrlString == null) return
        viewModelScope.launch {
            playTrackWithMediaNotificationUseCase.invoke(
                track,
                onLoading = { _uiState.value = ArtistDetailScreenUiState.Loading },
                onFinishedLoading = {
                    _uiState.value = if (it == null) ArtistDetailScreenUiState.PlayingTrack(track)
                    else ArtistDetailScreenUiState.Error("Unable to play track. Please check internet connection")
                }
            )
        }
    }
}