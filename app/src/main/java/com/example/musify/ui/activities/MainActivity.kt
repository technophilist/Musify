package com.example.musify.ui.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.musify.domain.Streamable
import com.example.musify.ui.navigation.MusifyBottomNavigationConnectedWithBackStack
import com.example.musify.ui.navigation.MusifyBottomNavigationDestinations
import com.example.musify.ui.navigation.MusifyNavigation
import com.example.musify.ui.screens.homescreen.ExpandableMiniPlayerWithSnackbar
import com.example.musify.ui.theme.MusifyTheme
import com.example.musify.viewmodels.PlaybackViewModel
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
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                    content = { MusifyApp() })
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
    val miniPlayerStreamable = remember(playbackState) {
        playbackState.currentlyPlayingStreamable ?: playbackState.previouslyPlayingStreamable
    }
    var isNowPlayingScreenVisible by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = playbackEvent) {
        if (playbackEvent !is PlaybackViewModel.Event.PlaybackError) return@LaunchedEffect
        snackbarHostState.currentSnackbarData?.dismiss()
        snackbarHostState.showSnackbar(
            message = (playbackEvent as PlaybackViewModel.Event.PlaybackError).errorMessage,
        )
    }
    val isPlaybackPaused = remember(playbackState) {
        playbackState is PlaybackViewModel.PlaybackState.Paused || playbackState is PlaybackViewModel.PlaybackState.PlaybackEnded
    }
    val onMiniPlayerPlayButtonClick = { streamable: Streamable ->
        if (playbackState is PlaybackViewModel.PlaybackState.Paused) {
            playbackViewModel.resumePlaybackIfPaused()
        } else if (playbackState is PlaybackViewModel.PlaybackState.PlaybackEnded) {
            // play the same track again
            playbackViewModel.playStreamable(streamable)
        }
    }
    BackHandler(isNowPlayingScreenVisible) {
        isNowPlayingScreenVisible = false
    }
    val bottomNavigationItems = remember {
        listOf(
            MusifyBottomNavigationDestinations.Home,
            MusifyBottomNavigationDestinations.Search,
            MusifyBottomNavigationDestinations.Premium
        )
    }
    val navController = rememberNavController()
    Box(modifier = Modifier.fillMaxSize()) {
        // the playbackState.currentlyPlayingTrack will automatically be set
        // to null when the playback is stopped
        MusifyNavigation(
            navController = navController,
            playStreamable = playbackViewModel::playStreamable,
            isFullScreenNowPlayingOverlayScreenVisible = isNowPlayingScreenVisible,
            onPausePlayback = playbackViewModel::pauseCurrentlyPlayingTrack
        )
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            AnimatedContent(
                modifier = Modifier.fillMaxWidth(),
                targetState = miniPlayerStreamable
            ) { state ->
                if (state == null) {
                    SnackbarHost(hostState = snackbarHostState)
                } else {
                    ExpandableMiniPlayerWithSnackbar(
                        modifier = Modifier
                            .animateEnterExit(
                                enter = fadeIn() + slideInVertically { it },
                                exit = fadeOut() + slideOutVertically { -it }
                            ),
                        streamable = miniPlayerStreamable!!,
                        onPauseButtonClicked = playbackViewModel::pauseCurrentlyPlayingTrack,
                        onPlayButtonClicked = { onMiniPlayerPlayButtonClick(it) },
                        isPlaybackPaused = isPlaybackPaused,
                        timeElapsedStringFlow = playbackViewModel.flowOfProgressTextOfCurrentTrack.value,
                        playbackProgressFlow = playbackViewModel.flowOfProgressOfCurrentTrack.value,
                        totalDurationOfCurrentTrackText = playbackViewModel.totalDurationOfCurrentTrackTimeText.value,
                        snackbarHostState = snackbarHostState
                    )
                }
            }

            MusifyBottomNavigationConnectedWithBackStack(
                navController = navController,
                modifier = Modifier.navigationBarsPadding(),
                navigationItems = bottomNavigationItems,
            )
        }
    }
}

