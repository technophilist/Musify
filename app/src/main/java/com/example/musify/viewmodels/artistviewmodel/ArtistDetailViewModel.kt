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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * An enum consisting of all UI states that are related to a screen
 * displaying the details of an artist.
 */
enum class ArtistDetailScreenUiState { IDLE, LOADING, ERROR }

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val repository: MusifyRepository,
) : AndroidViewModel(application) {

    private val _popularTracks = mutableStateOf<List<SearchResult.TrackSearchResult>>(emptyList())
    val popularTracks = _popularTracks as State<List<SearchResult.TrackSearchResult>>

    private val _uiState = mutableStateOf(ArtistDetailScreenUiState.IDLE)
    val uiState = _uiState as State<ArtistDetailScreenUiState>

    private val defaultMapperImageSize = MapperImageSize.MEDIUM
    private val artistId = savedStateHandle.get<String>(SAVED_STATE_ARTIST_ID_KEY)!!

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
        _uiState.value = ArtistDetailScreenUiState.LOADING
        val fetchResult = repository.fetchTopTenTracksForArtistWithId(
            artistId = artistId,
            imageSize = defaultMapperImageSize,
            countryCode = getCountryCode()
        )
        when (fetchResult) {
            is FetchedResource.Failure -> {
                _uiState.value = ArtistDetailScreenUiState.ERROR
            }
            is FetchedResource.Success -> {
                _popularTracks.value = fetchResult.data
                _uiState.value = ArtistDetailScreenUiState.IDLE
            }
        }
    }

    companion object {
        const val SAVED_STATE_ARTIST_ID_KEY =
            "com.example.musify.viewmodels.artistviewmodel.ArtistViewModel.ARTIST_ID_SAVED_STATE_NAV_ARG"
    }
}