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

    private fun createEventsListener(onEvents: (Player, Player.Events) -> Unit) =
        object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                onEvents(player, events)
            }
        }

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
            if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
                if (player.playbackState == Player.STATE_IDLE) {
                    onPlaybackStateChanged(MusicPlayer.PlaybackState.Idle)
                    return@createEventsListener
                }
                if (player.playbackState == Player.STATE_ENDED) {
                    currentlyPlayingTrack?.let {
                        onPlaybackStateChanged(MusicPlayer.PlaybackState.Ended(it))
                    }
                    return@createEventsListener
                }
            }

            if (events.contains(Player.EVENT_PLAYER_ERROR)) {
                onPlaybackStateChanged(MusicPlayer.PlaybackState.Error)
                return@createEventsListener
            }
            if (!events.contains(Player.EVENT_IS_PLAYING_CHANGED) && player.playbackState != Player.STATE_READY) return@createEventsListener
            currentlyPlayingTrack?.let {
                if (player.playWhenReady) onPlaybackStateChanged(
                    MusicPlayer.PlaybackState.Playing(it)
                )
                else onPlaybackStateChanged(MusicPlayer.PlaybackState.Paused(it))
            }
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