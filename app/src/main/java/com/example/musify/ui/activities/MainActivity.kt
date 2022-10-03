package com.example.musify.ui.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musify.domain.SearchResult
import com.example.musify.ui.components.MusifyBottomNavigation
import com.example.musify.ui.components.MusifyMiniPlayer
import com.example.musify.ui.navigation.MusifyBottomNavigationDestinations
import com.example.musify.ui.screens.MusifyNavigation
import com.example.musify.ui.screens.NowPlayingScreen
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                    content = { MusifyApp() }
                )
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
    val isPlaybackPaused = remember(playbackState) {
        playbackState is PlaybackViewModel.PlaybackState.Paused || playbackState is PlaybackViewModel.PlaybackState.PlaybackEnded
    }
    val onPlayButtonClicked = { track: SearchResult.TrackSearchResult ->
        if (playbackState is PlaybackViewModel.PlaybackState.Paused) {
            playbackViewModel.resumePlaybackIfPaused()
        } else if (playbackState is PlaybackViewModel.PlaybackState.PlaybackEnded) {
            // play the same track again
            playbackViewModel.playTrack(track)
        }
    }
    val totalDurationOfCurrentTrackText by playbackViewModel.totalDurationOfCurrentTrackTimeText
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
    var currentlySelectedBottomNavigationItem by remember {
        mutableStateOf<MusifyBottomNavigationDestinations>(MusifyBottomNavigationDestinations.Search)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        // the playbackState.currentlyPlayingTrack will automatically be set
        // to null when the playback is stopped
        MusifyNavigation(
            playTrack = playbackViewModel::playTrack,
            currentlyPlayingTrack = playbackState.currentlyPlayingTrack,
            isPlaybackLoading = playbackState is PlaybackViewModel.PlaybackState.Loading,
            isFullScreenNowPlayingOverlayScreenVisible = isNowPlayingScreenVisible
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            SnackbarHost(
                modifier = Modifier.padding(8.dp),
                hostState = snackbarHostState
            )
            androidx.compose.animation.AnimatedVisibility(
                visible = playbackState.currentlyPlayingTrack != null || playbackState.previouslyPlayingTrack != null,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { -it },
            ) {
                AnimatedContent(modifier = Modifier.fillMaxWidth(),
                    targetState = isNowPlayingScreenVisible,
                    transitionSpec = {
                        slideInVertically(animationSpec = tween()) with shrinkOut(animationSpec = tween())
                    }
                ) { isFullScreenVisible ->
                    miniPlayerTrack?.let {
                        if (isFullScreenVisible) {
                            NowPlayingScreen(
                                currentlyPlayingTrack = it,
                                isPlaybackPaused = isPlaybackPaused,
                                timeElapsedStringFlow = playbackViewModel.flowOfProgressTextOfCurrentTrack.value,
                                totalDurationOfCurrentTrackProvider = { totalDurationOfCurrentTrackText },
                                playbackDurationRange = PlaybackViewModel.PLAYBACK_PROGRESS_RANGE,
                                playbackProgressFlow = playbackViewModel.flowOfProgressOfCurrentTrack.value,
                                onCloseButtonClicked = { isNowPlayingScreenVisible = false },
                                onShuffleButtonClicked = { /*TODO*/ },
                                onSkipPreviousButtonClicked = { /*TODO*/ },
                                onPlayButtonClicked = { onPlayButtonClicked(miniPlayerTrack) },
                                onPauseButtonClicked = playbackViewModel::pauseCurrentlyPlayingTrack,
                                onSkipNextButtonClicked = { /*TODO*/ },
                                onRepeatButtonClicked = {}
                            )
                        } else {
                            MusifyMiniPlayer(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .clickable { isNowPlayingScreenVisible = true },
                                isPlaybackPaused = isPlaybackPaused,
                                currentlyPlayingTrack = it,
                                onLikedButtonClicked = {},
                                onPlayButtonClicked = { onPlayButtonClicked(miniPlayerTrack) },
                                onPauseButtonClicked = playbackViewModel::pauseCurrentlyPlayingTrack
                            )
                        }

                    }
                }
            }
            MusifyBottomNavigation(
                modifier = Modifier.navigationBarsPadding(),
                navigationItems = bottomNavigationItems,
                currentlySelectedItem = currentlySelectedBottomNavigationItem,
                onItemClick = { currentlySelectedBottomNavigationItem = it }
            )
        }
    }
}


