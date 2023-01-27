package com.example.musify.data.paging

import com.example.musify.data.remote.musicservice.SearchQueryType
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.toSearchResults
import com.example.musify.data.repositories.tokenrepository.TokenRepository
import com.example.musify.domain.SearchResult

@Suppress("FunctionName")
fun SpotifyTrackSearchPagingSource(
    searchQuery: String,
    countryCode: String,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService
): SpotifySearchPagingSource<SearchResult.TrackSearchResult> = SpotifySearchPagingSource(
    searchQuery = searchQuery,
    countryCode = countryCode,
    searchQueryType = SearchQueryType.TRACK,
    tokenRepository = tokenRepository,
    spotifyService = spotifyService,
    resultsBlock = { it.toSearchResults().tracks }
)

@Suppress("FunctionName")
fun SpotifyAlbumSearchPagingSource(
    searchQuery: String,
    countryCode: String,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService
): SpotifySearchPagingSource<SearchResult.AlbumSearchResult> = SpotifySearchPagingSource(
    searchQuery = searchQuery,
    countryCode = countryCode,
    searchQueryType = SearchQueryType.ALBUM,
    tokenRepository = tokenRepository,
    spotifyService = spotifyService,
    resultsBlock = { it.toSearchResults().albums }
)

@Suppress("FunctionName")
fun SpotifyArtistSearchPagingSource(
    searchQuery: String,
    countryCode: String,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService
): SpotifySearchPagingSource<SearchResult.ArtistSearchResult> = SpotifySearchPagingSource(
    searchQuery = searchQuery,
    countryCode = countryCode,
    searchQueryType = SearchQueryType.ARTIST,
    tokenRepository = tokenRepository,
    spotifyService = spotifyService,
    resultsBlock = { it.toSearchResults().artists }
)

@Suppress("FunctionName")
fun SpotifyPlaylistSearchPagingSource(
    searchQuery: String,
    countryCode: String,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService
): SpotifySearchPagingSource<SearchResult.PlaylistSearchResult> = SpotifySearchPagingSource(
    searchQuery = searchQuery,
    countryCode = countryCode,
    searchQueryType = SearchQueryType.PLAYLIST,
    tokenRepository = tokenRepository,
    spotifyService = spotifyService,
    resultsBlock = { it.toSearchResults().playlists }
)

@Suppress("FunctionName")
fun SpotifyPodcastSearchPagingSource(
    searchQuery: String,
    countryCode: String,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService
): SpotifySearchPagingSource<SearchResult.PodcastSearchResult> = SpotifySearchPagingSource(
    searchQuery = searchQuery,
    countryCode = countryCode,
    searchQueryType = SearchQueryType.SHOW,
    tokenRepository = tokenRepository,
    spotifyService = spotifyService,
    resultsBlock = { it.toSearchResults().shows }
)

@Suppress("FunctionName")
fun SpotifyEpisodeSearchPagingSource(
    searchQuery: String,
    countryCode: String,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService
): SpotifySearchPagingSource<SearchResult.EpisodeSearchResult> = SpotifySearchPagingSource(
    searchQuery = searchQuery,
    countryCode = countryCode,
    searchQueryType = SearchQueryType.EPISODE,
    tokenRepository = tokenRepository,
    spotifyService = spotifyService,
    resultsBlock = { it.toSearchResults().episodes }
)