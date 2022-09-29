package com.example.musify.viewmodels

import android.app.Application
import android.graphics.Bitmap
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    application: Application,
    private val musicPlayer: MusicPlayer,
    private val downloadDrawableFromUrlUseCase: DownloadDrawableFromUrlUseCase
) : AndroidViewModel(application) {

    // 0f to 100f
    private val _currentPlaybackProgress = mutableStateOf(0f)
    val currentPlaybackProgress = _currentPlaybackProgress as State<Float>

    private val _currentPlaybackProgressTimeText = mutableStateOf("00:00")
    val currentPlaybackProgressTimeText = _currentPlaybackProgressTimeText as State<String>

    private val _totalDurationOfCurrentTrackTimeText = mutableStateOf("00:00")
    val totalDurationOfCurrentTrackTimeText = _totalDurationOfCurrentTrackTimeText as State<String>

    private val _playbackState = mutableStateOf<PlaybackState>(PlaybackState.Idle)
    val playbackState = _playbackState as State<PlaybackState>

    private val _eventChannel = Channel<Event?>()
    val playbackEventsFlow = _eventChannel.receiveAsFlow()

    private val playbackErrorMessage = "An error occurred. Please check internet connection."

    init {
        musicPlayer.addOnPlaybackStateChangedListener {
            _playbackState.value = when (it) {
                is MusicPlayer.PlaybackState.Idle -> PlaybackState.Idle
                is MusicPlayer.PlaybackState.Playing -> {
                    _totalDurationOfCurrentTrackTimeText.value =
                        convertTimestampMillisToString(it.totalDuration)
                    it.currentPlaybackPositionInMillisFlow
                        .onEach { progress -> updateProgressForMillis(progress, it.totalDuration) }
                        .launchIn(viewModelScope)
                    PlaybackState.Playing(it.currentlyPlayingTrack.toTrackSearchResult())
                }
                is MusicPlayer.PlaybackState.Paused -> PlaybackState.Paused(it.currentlyPlayingTrack.toTrackSearchResult())
                is MusicPlayer.PlaybackState.Error -> {
                    viewModelScope.launch {
                        _eventChannel.send(Event.PlaybackError(playbackErrorMessage))
                    }
                    PlaybackState.Error(playbackErrorMessage)
                }
                is MusicPlayer.PlaybackState.Ended -> PlaybackState.PlaybackEnded(it.track.toTrackSearchResult())
            }
        }
    }

    fun playTrack(
        track: SearchResult.TrackSearchResult,
        onSuccess: ((SearchResult.TrackSearchResult, Bitmap) -> Unit)? = null
    ) {
        viewModelScope.launch {
            if (track.trackUrlString == null) {
                _eventChannel.send(Event.PlaybackError("This track is currently unavailable for playback."))
                return@launch
            }
            _playbackState.value =
                PlaybackState.Loading(previousTrack = _playbackState.value.currentlyPlayingTrack)
            val downloadAlbumArtResult = downloadDrawableFromUrlUseCase.invoke(
                urlString = track.imageUrlString, context = getApplication()
            )
            if (downloadAlbumArtResult.isSuccess) {
                val bitmap = downloadAlbumArtResult.getOrNull()!!.toBitmap()
                val musicPlayerTrack = track.toMusicPlayerTrack(bitmap)
                onSuccess?.invoke(track, bitmap)
                musicPlayer.playTrack(musicPlayerTrack)
            } else {
                _eventChannel.send(Event.PlaybackError(playbackErrorMessage))
                _playbackState.value = PlaybackState.Error(playbackErrorMessage)
            }
        }
    }

    fun pauseCurrentlyPlayingTrack() {
        musicPlayer.pauseCurrentlyPlayingTrack()
    }

    fun resumePlaybackIfPaused() {
        musicPlayer.tryResume()
    }

    private fun convertTimestampMillisToString(millis: Long): String = with(TimeUnit.MILLISECONDS) {
        // don't display the hour information if the track's duration is
        // less than an hour
        if (toHours(millis) == 0L) "%02d:%02d".format(
            toMinutes(millis),
            toSeconds(millis)
        )
        else "%02d%02d:%02d".format(
            toHours(millis),
            toMinutes(millis),
            toSeconds(millis)
        )
    }

    private fun updateProgressForMillis(progressMillis: Long, totalDurationMillis: Long) {
        _currentPlaybackProgress.value =
            (progressMillis.toFloat() / totalDurationMillis.toFloat()) * 100f
        _currentPlaybackProgressTimeText.value =
            convertTimestampMillisToString(progressMillis)
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayer.removeListenersIfAny()
    }

    companion object {
        val PLAYBACK_PROGRESS_RANGE = 0f..100f
    }

    sealed class PlaybackState(
        val currentlyPlayingTrack: SearchResult.TrackSearchResult? = null,
        val previouslyPlayingTrack: SearchResult.TrackSearchResult? = null
    ) {
        object Idle : PlaybackState()
        object Stopped : PlaybackState()
        data class Error(val errorMessage: String) : PlaybackState()
        data class Paused(val track: SearchResult.TrackSearchResult) : PlaybackState(track)
        data class Playing(val track: SearchResult.TrackSearchResult) : PlaybackState(track)
        data class PlaybackEnded(val track: SearchResult.TrackSearchResult) : PlaybackState(track)
        data class Loading(
            // track instance that indicates the track that was playing before
            // the state was changed to loading
            val previousTrack: SearchResult.TrackSearchResult?
        ) : PlaybackState(previouslyPlayingTrack = previousTrack)
    }

    sealed class Event {
        // a data class is not used because a 'Channel' will not send
        // two items of the same type consecutively. Since a data class
        // overrides equals & hashcode by default, if the same event
        // occurs consecutively, the event will not be sent over the
        // channel, resulting in missed events.
        class PlaybackError(val errorMessage: String) : Event()
    }
}
