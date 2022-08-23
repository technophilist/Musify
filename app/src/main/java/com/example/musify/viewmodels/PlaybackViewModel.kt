package com.example.musify.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musify.domain.SearchResult
import com.example.musify.domain.toMusicPlayerTrack
import com.example.musify.musicplayer.MusicPlayer
import com.example.musify.musicplayer.utils.toTrackSearchResult
import com.example.musify.usecases.downloadDrawableFromUrlUseCase.DownloadDrawableFromUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    application: Application,
    private val musicPlayer: MusicPlayer,
    private val downloadDrawableFromUrlUseCase: DownloadDrawableFromUrlUseCase
) : AndroidViewModel(application) {

    sealed class PlaybackState(val currentlyPlayingTrack: SearchResult.TrackSearchResult? = null) {
        object Idle : PlaybackState()
        object Paused : PlaybackState()
        object Stopped : PlaybackState()
        object Loading : PlaybackState()
        data class Error(val errorMessage: String) : PlaybackState()
        data class Playing(val track: SearchResult.TrackSearchResult) : PlaybackState(track)
    }

    sealed class Event {
        data class PlaybackError(val errorMessage: String) : Event()
    }

    private val _playbackState = mutableStateOf<PlaybackState>(PlaybackState.Idle)
    val playbackState = _playbackState as State<PlaybackState>

    private val playbackErrorMessage =
        "An error occurred while playing track. Please check internet connection."

    init {
        musicPlayer.addOnPlaybackStateChangedListener {
            _playbackState.value = when (it) {
                is MusicPlayer.PlaybackState.Idle -> PlaybackState.Idle
                is MusicPlayer.PlaybackState.Playing -> PlaybackState.Playing(it.currentlyPlayingTrack.toTrackSearchResult())
                is MusicPlayer.PlaybackState.Paused -> PlaybackState.Paused
                is MusicPlayer.PlaybackState.Stopped -> PlaybackState.Stopped
                is MusicPlayer.PlaybackState.Error -> PlaybackState.Error(playbackErrorMessage)
            }
        }
    }

    fun playTrack(track: SearchResult.TrackSearchResult) {
        if (track.trackUrlString == null) {
            _playbackState.value =
                PlaybackState.Error("This track is currently not available for playback.")
            return
        }
        viewModelScope.launch {
            _playbackState.value = PlaybackState.Loading
            val downloadAlbumArtResult = downloadDrawableFromUrlUseCase.invoke(
                urlString = track.imageUrlString,
                application = getApplication()
            )
            if (downloadAlbumArtResult.isSuccess) {
                val musicPlayerTrack =
                    track.toMusicPlayerTrack(downloadAlbumArtResult.getOrNull()!!.toBitmap())
                musicPlayer.playTrack(musicPlayerTrack)
            } else {
                _playbackState.value = PlaybackState.Error(playbackErrorMessage)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayer.removeListenersIfAny()
    }
}
