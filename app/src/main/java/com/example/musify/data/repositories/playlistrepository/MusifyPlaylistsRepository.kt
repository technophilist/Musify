package com.example.musify.data.repositories.playlistrepository

import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.toPlaylistSearchResult
import com.example.musify.data.repositories.tokenrepository.TokenRepository
import com.example.musify.data.repositories.tokenrepository.runCatchingWithToken
import com.example.musify.data.utils.FetchedResource
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.SearchResult
import javax.inject.Inject

class MusifyPlaylistsRepository @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val spotifyService: SpotifyService
) : PlaylistsRepository {
    override suspend fun fetchPlaylistWithId(
        playlistId: String,
        countryCode: String
    ): FetchedResource<SearchResult.PlaylistSearchResult, MusifyErrorType> =
        tokenRepository.runCatchingWithToken {
            spotifyService.getPlaylistWithId(playlistId, countryCode, it).toPlaylistSearchResult()
        }

}