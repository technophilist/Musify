package com.example.musify.data.repositories.playlistrepository

import com.example.musify.data.utils.FetchedResource
import com.example.musify.domain.MusicSummary
import com.example.musify.domain.MusifyErrorType

interface PlaylistsRepository {
    suspend fun fetchPlaylistWithId(
        playlistId: String,
        countryCode: String
    ): FetchedResource<MusicSummary.PlaylistSummary, MusifyErrorType>
}