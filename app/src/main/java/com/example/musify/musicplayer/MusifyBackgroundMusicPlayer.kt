package com.example.musify.musicplayer

import android.content.Context
import com.example.musify.R
import com.example.musify.musicplayer.utils.MediaDescriptionAdapter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.util.NotificationUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class MusifyBackgroundMusicPlayer @Inject constructor(
    @ApplicationContext context: Context, private val exoPlayer: ExoPlayer
) : MusicPlayer {
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
    private var listener: Player.Listener? = null
    private var currentPlaybackState: MusicPlayer.PlaybackState? = null

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
        currentPlaybackPositionInMillisFlow = flow {
            while (player.currentPosition <= player.duration) {
                emit(player.currentPosition)
                delay(1_000)
            }
            // when paused, the same value will be emitted, to prevent the
            // emission of the same value, use distinctUntilChanged
        }.distinctUntilChanged()
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

    override fun addOnPlaybackStateChangedListener(onPlaybackStateChanged: (MusicPlayer.PlaybackState) -> Unit) {
        listener = createEventsListener { player, events ->
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
            // This callback can be called multiple times on events that may
            // not be of relevance. This may lead to the generation of a new
            // state that is equivalent to old state. To prevent invocation of
            // the user defined onPlaybackStateChanged callback with the same state
            // more than once, compare it to the previous state the callback was
            // invoked with. Invoke the user defined callback if and only if the
            // previous  state is not the same as the newly generated state.
            if (currentPlaybackState == newPlaybackState) return@createEventsListener
            currentPlaybackState = newPlaybackState
            onPlaybackStateChanged(newPlaybackState)
        }
        exoPlayer.addListener(listener!!)
    }

    override fun removeListenersIfAny() {
        listener?.let(exoPlayer::removeListener)
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