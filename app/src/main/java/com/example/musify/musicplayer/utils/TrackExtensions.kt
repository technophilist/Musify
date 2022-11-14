package com.example.musify.musicplayer.utils

import com.example.musify.domain.SearchResult
import com.example.musify.musicplayer.MusicPlayerV2

/**
 * A mapper method used to map an instance of [MusicPlayer.Track]
 * to an instance of [SearchResult.TrackSearchResult].
 */
fun MusicPlayerV2.Track.toTrackSearchResult() = SearchResult.TrackSearchResult(
    id = id,
    name = title,
    imageUrlString = albumArtUrlString,
    artistsString = artistsString,
    trackUrlString = trackUrlString
)