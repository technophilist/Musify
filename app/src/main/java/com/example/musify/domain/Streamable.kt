package com.example.musify.domain

import android.graphics.Bitmap
import com.example.musify.musicplayer.MusicPlayerV2

/**
 * Indicates a class that contains a streamable url. It is possible
 * that a class may contain a null [streamUrl] because the
 * link might not be available for that specific instance.
 * For example, an API might return a list of tracks with a nullable
 * preview url, where the preview url for certain tracks might be null.
 */
sealed interface Streamable {
    val streamUrl: String?
}

/**
 * A mapper method used to map an instance of [Streamable] to an instance of [MusicPlayerV2.Track].
 */
fun Streamable.toMusicPlayerTrack(albumArtBitmap: Bitmap): MusicPlayerV2.Track = when (this) {
    is PodcastEpisode -> toMusicPlayerTrack(albumArtBitmap) // PodcastEpisode#toMusicPlayerTrack
    is SearchResult.TrackSearchResult -> toMusicPlayerTrack(albumArtBitmap) // TrackSearchResult#toMusicPlayerTrack
}
