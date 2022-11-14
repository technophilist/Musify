package com.example.musify.musicplayer

import android.content.Context
import com.example.musify.R
import com.example.musify.musicplayer.utils.MediaDescriptionAdapter
import com.example.musify.musicplayer.utils.getCurrentPlaybackProgressFlow
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.util.NotificationUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject


class MusifyBackgroundMusicPlayerV2 @Inject constructor(
    @ApplicationContext context: Context,
    private val exoPlayer: ExoPlayer
) : MusicPlayerV2 {
    private var currentlyPlayingTrack: MusicPlayer.Track? = null
    private val notificationManagerBuilder by lazy {
        PlayerNotificationManager.Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            .setChannelImportance(NotificationUtil.IMPORTANCE_LOW).setMediaDescriptionAdapter(
                MediaDescriptionAdapter(getCurrentContentText = {
                    currentlyPlayingTrack?.artistsString ?: ""
                },
                    getCurrentContentTitle = { currentlyPlayingTrack?.title ?: "" },
                    getCurrentLargeIcon = { _, _ -> currentlyPlayingTrack?.albumArt })
            ).setChannelNameResourceId(R.string.notification_channel_name)
            .setChannelDescriptionResourceId(R.string.notification_channel_description)
    }

    override fun getCurrentPlaybackStateStream() = callbackFlow {
        val listener = createEventsListener { player, events ->
            if (!events.containsAny(
                    Player.EVENT_PLAYBACK_STATE_CHANGED,
                    Player.EVENT_PLAYER_ERROR,
                    Player.EVENT_IS_PLAYING_CHANGED
                )
            ) return@createEventsListener
            val isPlaying =
                events.contains(Player.EVENT_IS_PLAYING_CHANGED) && player.playbackState == Player.STATE_READY && player.playWhenReady
            val isPaused =
                events.contains(Player.EVENT_IS_PLAYING_CHANGED) && player.playbackState == Player.STATE_READY && !player.playWhenReady
            val newPlaybackState = when {
                events.contains(Player.EVENT_PLAYER_ERROR) -> MusicPlayer.PlaybackState.Error
                isPlaying -> currentlyPlayingTrack?.let { buildPlayingState(it, player) }
                isPaused -> currentlyPlayingTrack?.let(MusicPlayer.PlaybackState::Paused)
                player.playbackState == Player.STATE_IDLE -> MusicPlayer.PlaybackState.Idle
                player.playbackState == Player.STATE_ENDED -> currentlyPlayingTrack?.let(MusicPlayer.PlaybackState::Ended)
                else -> null
            } ?: return@createEventsListener
            trySend(newPlaybackState)
        }
        exoPlayer.addListener(listener)
        awaitClose { exoPlayer.removeListener(listener) }
        // This callback can be called multiple times on events that may
        // not be of relevance. This may lead to the generation of a new
        // state that is equivalent to the old state. Therefore use
        // distinctUntilChanged
    }.distinctUntilChanged()

    private fun createEventsListener(onEvents: (Player, Player.Events) -> Unit) =
        object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                onEvents(player, events)
            }
        }

    private fun buildPlayingState(
        track: MusicPlayer.Track,
        player: Player,
    ) = MusicPlayer.PlaybackState.Playing(
        currentlyPlayingTrack = track,
        totalDuration = player.duration,
        currentPlaybackPositionInMillisFlow = player.getCurrentPlaybackProgressFlow()
    )

    override fun playTrack(track: MusicPlayer.Track) {
        with(exoPlayer) {
            if (currentlyPlayingTrack == track) {
                seekTo(0)
                return@with
            }
            if (isPlaying) exoPlayer.stop()
            currentlyPlayingTrack = track
            setMediaItem(MediaItem.fromUri(track.trackUrlString))
            prepare()
            notificationManagerBuilder.build().setPlayer(exoPlayer)
            play()
        }
    }

    override fun pauseCurrentlyPlayingTrack() {
        exoPlayer.pause()
    }

    override fun stopPlayingTrack() {
        exoPlayer.stop()
    }

    override fun tryResume(): Boolean {
        if (exoPlayer.isPlaying) return false
        return currentlyPlayingTrack?.let {
            exoPlayer.playWhenReady = true
            true
        } ?: false
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID =
            "com.example.musify.musicplayer.MusicPlayerService.NOTIFICATION_CHANNEL_ID"
        private const val NOTIFICATION_ID = 1
    }
}