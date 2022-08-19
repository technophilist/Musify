package com.example.musify.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musify.ui.screens.MusifyNavigation
import com.example.musify.ui.theme.MusifyTheme
import com.example.musify.viewmodels.PlaybackViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusifyTheme {
                ProvideWindowInsets {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background,
                        content = { MusifyApp() }
                    )
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
private fun MusifyApp() {
    val playbackViewModel = hiltViewModel<PlaybackViewModel>()
    val playbackState by playbackViewModel.playbackState

    MusifyNavigation(
        currentlyPlayingTrack = playbackState.currentlyPlayingTrack,
        isPaused = playbackState is PlaybackViewModel.PlaybackState.Paused,
        playTrack = playbackViewModel::playTrack
    )
}

