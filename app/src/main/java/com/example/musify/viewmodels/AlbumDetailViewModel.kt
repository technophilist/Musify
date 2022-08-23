package com.example.musify.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.musify.data.repository.Repository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.di.MusifyApplication
import com.example.musify.domain.SearchResult
import com.example.musify.ui.navigation.MusifyNavigationDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val repository: Repository,
) : AndroidViewModel(application) {

    private val _tracks = mutableStateOf<List<SearchResult.TrackSearchResult>>(emptyList())
    val tracks = _tracks as State<List<SearchResult.TrackSearchResult>>

    private val _uiState = mutableStateOf<AlbumDetailUiState>(AlbumDetailUiState.Idle)
    val uiState = _uiState as State<AlbumDetailUiState>

    private val albumId =
        savedStateHandle.get<String>(MusifyNavigationDestinations.AlbumDetailScreen.NAV_ARG_ALBUM_ID)!!
    private val defaultMapperImageSize = MapperImageSize.MEDIUM

    init {
        fetchAndAssignTrackList()
    }

    private fun getCountryCode(): String = getApplication<MusifyApplication>()
        .resources
        .configuration
        .locale
        .country

    private fun fetchAndAssignTrackList() {
        viewModelScope.launch {
            _uiState.value = AlbumDetailUiState.Loading
            val result = repository.fetchTracksForAlbumWithId(
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