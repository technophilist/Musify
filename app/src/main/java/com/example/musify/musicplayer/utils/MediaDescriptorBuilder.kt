package com.example.musify.musicplayer.utils

import android.app.PendingIntent
import android.graphics.Bitmap
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

fun MediaDescriptionAdapter(
    getCurrentContentTitle: (Player) -> CharSequence,
    getCurrentContentText: (Player) -> CharSequence,
    getCurrentLargeIcon: (Player, PlayerNotificationManager.BitmapCallback) -> Bitmap?,
    createCurrentContentIntent: ((Player) -> PendingIntent?)? = null,
) = object : PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): CharSequence =
        getCurrentContentTitle(player)

    override fun createCurrentContentIntent(player: Player): PendingIntent? =
        createCurrentContentIntent?.invoke(player)

    override fun getCurrentContentText(player: Player): CharSequence = getCurrentContentText(player)

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? = getCurrentLargeIcon(player, callback)
}