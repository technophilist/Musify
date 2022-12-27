package com.example.musify.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musify.domain.PodcastEpisode
import com.example.musify.domain.SearchResult
import com.example.musify.domain.Streamable
import com.example.musify.musicplayer.MusicPlayerV2
import com.example.musify.usecases.downloadDrawableFromUrlUseCase.DownloadDrawableFromUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    application: Application,
    private val musicPlayer: MusicPlayerV2,
    private val downloadDrawableFromUrlUseCase: DownloadDrawableFromUrlUseCase
) : AndroidViewModel(application) {

    private val _totalDurationOfCurrentTrackTimeText = mutableStateOf("00:00")
    val totalDurationOfCurrentTrackTimeText = _totalDurationOfCurrentTrackTimeText as State<String>

    private val _playbackState = mutableStateOf<PlaybackState>(PlaybackState.Idle)
    val playbackState = _playbackState as State<PlaybackState>

    private val _eventChannel = Channel<Event?>()
    val playbackEventsFlow = _eventChannel.receiveAsFlow()

    // 0f to 100f
    val flowOfProgressOfCurrentTrack = mutableStateOf<Flow<Float>>(emptyFlow())
    val flowOfProgressTextOfCurrentTrack = mutableStateOf<Flow<String>>(emptyFlow())

    private val playbackErrorMessage = "An error occurred. Please check internet connection."

    init {
        musicPlayer.currentPlaybackStateStream.onEach {
            _playbackState.value = when (it) {
                is MusicPlayerV2.PlaybackState.Loading -> PlaybackState.Loading(it.previouslyPlayingStreamable)
                is MusicPlayerV2.PlaybackState.Idle -> PlaybackState.Idle
                is MusicPlayerV2.PlaybackState.Playing -> {
                    _totalDurationOfCurrentTrackTimeText.value =
                        convertTimestampMillisToString(it.totalDuration)
                    flowOfProgressOfCurrentTrack.value =
                        it.currentPlaybackPositionInMillisFlow.map { progress -> (progress.toFloat() / it.totalDuration) * 100f }
                    flowOfProgressTextOfCurrentTrack.value =
                        it.currentPlaybackPositionInMillisFlow.map(::convertTimestampMillisToString)
                    PlaybackState.Playing(it.currentlyPlayingStreamable)
                }
                is MusicPlayerV2.PlaybackState.Paused -> PlaybackState.Paused(it.currentlyPlayingStreamable)
                is MusicPlayerV2.PlaybackState.Error -> {
                    viewModelScope.launch {
                        _eventChannel.send(Event.PlaybackError(playbackErrorMessage))
                    }
                    PlaybackState.Error(playbackErrorMessage)
                }
                is MusicPlayerV2.PlaybackState.Ended -> PlaybackState.PlaybackEnded(it.streamable)
            }
        }.launchIn(viewModelScope)
    }

    fun playStreamable(streamable: Streamable) {
        viewModelScope.launch {
            if (streamable.streamUrl == null) {
                val streamableType = when (streamable) {
                    is PodcastEpisode -> "podcast episode"
                    is SearchResult.TrackSearchResult -> "track"
                }
                _eventChannel.send(Event.PlaybackError("This $streamableType is currently unavailable for playback."))
                return@launch
            }
            val imageUrlString = when (streamable) {
                is PodcastEpisode -> streamable.podcastInfo.imageUrl
                is SearchResult.TrackSearchResult -> streamable.imageUrlString
            }
            val downloadAlbumArtResult = downloadDrawableFromUrlUseCase.invoke(
                urlString = imageUrlString,
                context = getApplication()
            )
            if (downloadAlbumArtResult.isSuccess) {
                val bitmap = downloadAlbumArtResult.getOrNull()!!.toBitmap() // TODO check
                musicPlayer.playStreamable(
                    streamable = streamable,
                    associatedAlbumArt = bitmap
                )
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
            toMinutes(millis), toSeconds(millis)
        )
        else "%02d%02d:%02d".format(
            toHours(millis), toMinutes(millis), toSeconds(millis)
        )
    }

    companion object {
        val PLAYBACK_PROGRESS_RANGE = 0f..100f
    }

    sealed class PlaybackState(
        val currentlyPlayingStreamable: Streamable? = null,
        val previouslyPlayingStreamable: Streamable? = null
    ) {
        object Idle : PlaybackState()
        object Stopped : PlaybackState()
        data class Error(val errorMessage: String) : PlaybackState()
        data class Paused(val streamable: Streamable) : PlaybackState(streamable)
        data class Playing(val streamable: Streamable) : PlaybackState(streamable)
        data class PlaybackEnded(val streamable: Streamable) : PlaybackState(streamable)
        data class Loading(
            // Streamable instance that indicates the track that was playing before
            // the state was changed to loading
            val previousStreamable: Streamable?
        ) : PlaybackState(previouslyPlayingStreamable = previousStreamable)
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
