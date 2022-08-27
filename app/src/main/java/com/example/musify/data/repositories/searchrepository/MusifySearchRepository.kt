package com.example.musify.data.repositories.searchrepository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.musify.data.paging.SpotifyAlbumSearchPagingSource
import com.example.musify.data.paging.SpotifyArtistSearchPagingSource
import com.example.musify.data.paging.SpotifyPlaylistSearchPagingSource
import com.example.musify.data.paging.SpotifyTrackSearchPagingSource
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.toSearchResults
import com.example.musify.data.repository.tokenrepository.TokenRepository
import com.example.musify.data.repository.tokenrepository.runCatchingWithToken
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.SearchResult
import com.example.musify.domain.SearchResults
import kotlinx.coroutines.flow.Flow

class MusifySearchRepository(
    private val tokenRepository: TokenRepository,
    private val spotifyService: SpotifyService,
    private val pagingConfig: PagingConfig
) : SearchRepository {
    override suspend fun fetchSearchResultsForQuery(
        searchQuery: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<SearchResults, MusifyErrorType> = tokenRepository.runCatchingWithToken {
        spotifyService.search(searchQuery, countryCode, it).toSearchResults(imageSize)
    }

    override fun getPaginatedSearchStreamForAlbums(
        searchQuery: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<SearchResult.AlbumSearchResult>> = Pager(pagingConfig) {
        SpotifyAlbumSearchPagingSource(
            searchQuery = searchQuery,
            countryCode = countryCode,
            imageSize = imageSize,
            tokenRepository = tokenRepository,
            spotifyService = spotifyService
        )
    }.flow

    override fun getPaginatedSearchStreamForArtists(
        searchQuery: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<SearchResult.ArtistSearchResult>> = Pager(pagingConfig) {
        SpotifyArtistSearchPagingSource(
            searchQuery = searchQuery,
            countryCode = countryCode,
            imageSize = imageSize,
            tokenRepository = tokenRepository,
            spotifyService = spotifyService
        )
    }.flow

    override fun getPaginatedSearchStreamForTracks(
        searchQuery: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<SearchResult.TrackSearchResult>> = Pager(pagingConfig) {
        SpotifyTrackSearchPagingSource(
            searchQuery = searchQuery,
            countryCode = countryCode,
            imageSize = imageSize,
            tokenRepository = tokenRepository,
            spotifyService = spotifyService
        )
    }.flow

    override fun getPaginatedSearchStreamForPlaylists(
        searchQuery: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<SearchResult.PlaylistSearchResult>> = Pager(pagingConfig) {
        SpotifyPlaylistSearchPagingSource(
            searchQuery = searchQuery,
            countryCode = countryCode,
            imageSize = imageSize,
            tokenRepository = tokenRepository,
            spotifyService = spotifyService
        )
    }.flow
}