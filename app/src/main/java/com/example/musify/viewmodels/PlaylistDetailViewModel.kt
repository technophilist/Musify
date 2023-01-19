package com.example.musify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.musify.data.repositories.tracksrepository.TracksRepository
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.ui.navigation.MusifyNavigationDestinations
import com.example.musify.usecases.getCurrentlyPlayingTrackUseCase.GetCurrentlyPlayingTrackUseCase
import com.example.musify.usecases.getPlaybackLoadingStatusUseCase.GetPlaybackLoadingStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    tracksRepository: TracksRepository,
    getCurrentlyPlayingTrackUseCase: GetCurrentlyPlayingTrackUseCase,
    getPlaybackLoadingStatusUseCase: GetPlaybackLoadingStatusUseCase,
) : AndroidViewModel(application) {
    private val playlistId =
        savedStateHandle.get<String>(MusifyNavigationDestinations.PlaylistDetailScreen.NAV_ARG_PLAYLIST_ID)!!
    val playbackLoadingStateStream = getPlaybackLoadingStatusUseCase.loadingStatusStream
    val currentlyPlayingTrackStream = getCurrentlyPlayingTrackUseCase.currentlyPlayingTrackStream
    val tracks = tracksRepository.getPaginatedStreamForPlaylistTracks(
        playlistId = playlistId,
        countryCode = getCountryCode(),
        imageSize = MapperImageSize.MEDIUM
    ).cachedIn(viewModelScope)
}