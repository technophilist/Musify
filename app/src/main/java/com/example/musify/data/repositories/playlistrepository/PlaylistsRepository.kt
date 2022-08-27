package com.example.musify.data.repositories.playlistrepository

import com.example.musify.data.utils.FetchedResource
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.SearchResult

interface PlaylistsRepository {
    suspend fun fetchPlaylistWithId(
        playlistId: String,
        countryCode: String
    ): FetchedResource<SearchResult.PlaylistSearchResult, MusifyErrorType>
}