package com.example.musify.data.paging

import com.example.musify.data.remote.musicservice.SearchQueryType
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.toSearchResults
import com.example.musify.data.repositories.tokenrepository.TokenRepository
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.SearchResult

@Suppress("FunctionName")
fun SpotifyTrackSearchPagingSource(
    searchQuery: String,
    countryCode: String,
    imageSize: MapperImageSize,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService
): SpotifySearchPagingSource<SearchResult.TrackSearchResult> = SpotifySearchPagingSource(
    searchQuery = searchQuery,
    countryCode = countryCode,
    searchQueryType = SearchQueryType.TRACK,
    tokenRepository = tokenRepository,
    spotifyService = spotifyService,
    resultsBlock = { it.toSearchResults(imageSize).tracks }
)

@Suppress("FunctionName")
fun SpotifyAlbumSearchPagingSource(
    searchQuery: String,
    countryCode: String,
    imageSize: MapperImageSize,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService
): SpotifySearchPagingSource<SearchResult.AlbumSearchResult> = SpotifySearchPagingSource(
    searchQuery = searchQuery,
    countryCode = countryCode,
    searchQueryType = SearchQueryType.ALBUM,
    tokenRepository = tokenRepository,
    spotifyService = spotifyService,
    resultsBlock = { it.toSearchResults(imageSize).albums }
)

@Suppress("FunctionName")
fun SpotifyArtistSearchPagingSource(
    searchQuery: String,
    countryCode: String,
    imageSize: MapperImageSize,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService
): SpotifySearchPagingSource<SearchResult.ArtistSearchResult> = SpotifySearchPagingSource(
    searchQuery = searchQuery,
    countryCode = countryCode,
    searchQueryType = SearchQueryType.ARTIST,
    tokenRepository = tokenRepository,
    spotifyService = spotifyService,
    resultsBlock = { it.toSearchResults(imageSize).artists }
)

@Suppress("FunctionName")
fun SpotifyPlaylistSearchPagingSource(
    searchQuery: String,
    countryCode: String,
    imageSize: MapperImageSize,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService
): SpotifySearchPagingSource<SearchResult.PlaylistSearchResult> = SpotifySearchPagingSource(
    searchQuery = searchQuery,
    countryCode = countryCode,
    searchQueryType = SearchQueryType.PLAYLIST,
    tokenRepository = tokenRepository,
    spotifyService = spotifyService,
    resultsBlock = { it.toSearchResults(imageSize).playlists }
)

@Suppress("FunctionName")
fun SpotifyPodcastSearchPagingSource(
    searchQuery: String,
    countryCode: String,
    imageSize: MapperImageSize,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService
): SpotifySearchPagingSource<SearchResult.PodcastSearchResult> = SpotifySearchPagingSource(
    searchQuery = searchQuery,
    countryCode = countryCode,
    searchQueryType = SearchQueryType.SHOW,
    tokenRepository = tokenRepository,
    spotifyService = spotifyService,
    resultsBlock = { it.toSearchResults(imageSize).shows }
)