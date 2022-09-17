package com.example.musify.musicplayer

import android.content.Context
import com.example.musify.R
import com.example.musify.musicplayer.utils.MediaDescriptionAdapter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
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

    override fun playTrack(track: MusicPlayer.Track) {
        with(exoPlayer) {
            if (isPlaying) exoPlayer.stop()
            currentlyPlayingTrack = track
            setMediaItem(MediaItem.fromUri(track.trackUrlString))
            prepare()
            notificationManagerBuilder.build().apply { setPlayer(exoPlayer) }
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
        listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_IDLE -> onPlaybackStateChanged(MusicPlayer.PlaybackState.Idle)
                    Player.STATE_BUFFERING -> {}
                    Player.STATE_ENDED -> {}
                    Player.STATE_READY -> {
                        if (!exoPlayer.playWhenReady) onPlaybackStateChanged(MusicPlayer.PlaybackState.Paused)
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                onPlaybackStateChanged(MusicPlayer.PlaybackState.Error)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (!isPlaying) return
                val state = currentlyPlayingTrack?.let(MusicPlayer.PlaybackState::Playing) ?: return
                onPlaybackStateChanged(state)
            }
        }
        exoPlayer.addListener(listener!!)
    }

    override fun removeListenersIfAny() {
        listener?.let(exoPlayer::removeListener)
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID =
            "com.example.musify.musicplayer.MusicPlayerService.NOTIFICATION_CHANNEL_ID"
        private const val NOTIFICATION_ID = 1
    }
}