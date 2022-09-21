package com.example.musify.ui.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musify.ui.components.MusifyMiniPlayer
import com.example.musify.ui.screens.MusifyNavigation
import com.example.musify.ui.screens.NowPlayingScreen
import com.example.musify.ui.theme.MusifyTheme
import com.example.musify.viewmodels.PlaybackViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContent {
            MusifyTheme {
                ProvideWindowInsets {
                    Surface(modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background,
                        content = { MusifyApp() })
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
private fun MusifyApp() {
    val playbackViewModel = hiltViewModel<PlaybackViewModel>()
    val playbackState by playbackViewModel.playbackState
    val snackbarHostState = remember { SnackbarHostState() }
    val playbackEvent: PlaybackViewModel.Event? by playbackViewModel.playbackEventsFlow.collectAsState(
        initial = null
    )
    val miniPlayerTrack = remember(playbackState) {
        playbackState.currentlyPlayingTrack ?: playbackState.previouslyPlayingTrack
    }
    var isNowPlayingScreenVisible by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = playbackEvent) {
        if (playbackEvent !is PlaybackViewModel.Event.PlaybackError) return@LaunchedEffect
        snackbarHostState.currentSnackbarData?.dismiss()
        snackbarHostState.showSnackbar(
            message = (playbackEvent as PlaybackViewModel.Event.PlaybackError).errorMessage,
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        // the playbackState.currentlyPlayingTrack will automatically be set
        // to null when the playback is stopped
        MusifyNavigation(
            playTrack = playbackViewModel::playTrack,
            currentlyPlayingTrack = playbackState.currentlyPlayingTrack,
            isPlaybackLoading = playbackState is PlaybackViewModel.PlaybackState.Loading
        )
        SnackbarHost(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(8.dp),
            hostState = snackbarHostState
        )
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .animateContentSize(animationSpec = tween()),
            visible = playbackState.currentlyPlayingTrack != null || playbackState.previouslyPlayingTrack != null,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { -it },
        ) {
            miniPlayerTrack?.let {
                if (isNowPlayingScreenVisible) {
                    NowPlayingScreen(currentlyPlayingTrack = it,
                        playbackDurationRange = 0f..1f, // TODO
                        playbackProgressProvider = { 0.5f },
                        onCloseButtonClicked = { isNowPlayingScreenVisible = false },
                        onShuffleButtonClicked = { /*TODO*/ },
                        onSkipPreviousButtonClicked = { /*TODO*/ },
                        onPlayButtonClicked = { /*TODO*/ },
                        onPauseButtonClicked = { /*TODO*/ },
                        onSkipNextButtonClicked = { /*TODO*/ }) {}
                } else {
                    MusifyMiniPlayer(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 8.dp)
                            .clickable { isNowPlayingScreenVisible = true },
                        isPlaybackPaused = playbackState is PlaybackViewModel.PlaybackState.Paused || playbackState is PlaybackViewModel.PlaybackState.PlaybackEnded,
                        currentlyPlayingTrack = it,
                        onLikedButtonClicked = {},
                        onPlayButtonClicked = {
                            if (playbackState is PlaybackViewModel.PlaybackState.Paused) {
                                playbackViewModel.resumePlaybackIfPaused()
                                return@MusifyMiniPlayer
                            }
                            if (playbackState is PlaybackViewModel.PlaybackState.PlaybackEnded) {
                                // play the same track again
                                playbackViewModel.playTrack(it)
                            }
                        },
                        onPauseButtonClicked = playbackViewModel::pauseCurrentlyPlayingTrack
                    )
                }
            }
        }
    }
}


