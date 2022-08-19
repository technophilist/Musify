package com.example.musify.viewmodels

import android.app.Application
import com.example.musify.R
import com.example.musify.domain.fakeTrackSearchResult
import com.example.musify.musicPlayer.MusicPlayerMock
import com.example.musify.musicplayer.MusicPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class PlaybackViewModelTest {

    private lateinit var playbackViewModelWithSuccessfulImageFetch: PlaybackViewModel
    private lateinit var playbackViewModelWithFailingImageFetch: PlaybackViewModel
    private lateinit var musicPlayer: MusicPlayer
    private lateinit var application: Application

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        application = RuntimeEnvironment.getApplication()
        musicPlayer = MusicPlayerMock()
        playbackViewModelWithSuccessfulImageFetch = PlaybackViewModel(
            application = application,
            musicPlayer = musicPlayer,
            downloadDrawableFromUrlUseCase = { _, _ ->
                Result.success(
                    RuntimeEnvironment.getApplication()
                        .getDrawable(R.drawable.ic_outline_account_circle_24)!!
                )
            }
        )
        playbackViewModelWithFailingImageFetch = PlaybackViewModel(
            application = application,
            musicPlayer = MusicPlayerMock(),
            downloadDrawableFromUrlUseCase = { _, _ -> Result.failure(IOException()) },
        )
    }

    @Test
    fun playTrackTest_trackWithTrackUrlStringSetToNull_updatesStateToError() {
        val track = fakeTrackSearchResult.copy(trackUrlString = null)
        playbackViewModelWithSuccessfulImageFetch.playTrack(track)
        assert(playbackViewModelWithSuccessfulImageFetch.playbackState.value is PlaybackViewModel.PlaybackState.Error)
    }

    @Test
    fun playTrackTest_trackWithTrackUrlString_withImageFetchAlwaysSucceeding_updatesStateToPlaying() {
        val track = fakeTrackSearchResult
        playbackViewModelWithSuccessfulImageFetch.playTrack(track)
        assert(playbackViewModelWithSuccessfulImageFetch.playbackState.value is PlaybackViewModel.PlaybackState.Playing)
        assert((playbackViewModelWithSuccessfulImageFetch.playbackState.value as PlaybackViewModel.PlaybackState.Playing).currentlyPlayingTrack == track)
    }

    @Test
    fun playTrackTest_trackWithTrackUrlString_withImageFetchAlwaysFailing_updatesStateToError() {
        val track = fakeTrackSearchResult
        playbackViewModelWithFailingImageFetch.playTrack(track)
        assert(playbackViewModelWithFailingImageFetch.playbackState.value is PlaybackViewModel.PlaybackState.Error)
    }

    @Test
    fun updateStateTest_whenUnderlyingMusicPlayerStateChanges_viewModelStateMustAlsoCorrectlyChange() {
        // pause
        musicPlayer.pauseCurrentlyPlayingTrack()
        assert(playbackViewModelWithSuccessfulImageFetch.playbackState.value is PlaybackViewModel.PlaybackState.Paused)
        // stop
        musicPlayer.stopPlayingTrack()
        assert(playbackViewModelWithSuccessfulImageFetch.playbackState.value is PlaybackViewModel.PlaybackState.Stopped)
    }

}