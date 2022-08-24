package com.example.musify.data.repository

import androidx.paging.PagingData
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.*
import kotlinx.coroutines.flow.Flow

/**
 * An interface the consists of all the methods that are a requisite for
 * an instance of [Repository].
 */
interface Repository {
    enum class PaginatedStreamType { ALBUMS, ARTISTS, TRACKS, PLAYLISTS }

    suspend fun fetchArtistSummaryForId(
        artistId: String,
        imageSize: MapperImageSize
    ): FetchedResource<MusicSummary.ArtistSummary, MusifyHttpErrorType>

    suspend fun fetchAlbumsOfArtistWithId(
        artistId: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<List<MusicSummary.AlbumSummary>, MusifyHttpErrorType>

    suspend fun fetchTopTenTracksForArtistWithId(
        artistId: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<List<SearchResult.TrackSearchResult>, MusifyHttpErrorType>

    suspend fun fetchAlbumWithId(
        albumId: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<MusicSummary.AlbumSummary, MusifyHttpErrorType>

    suspend fun fetchPlaylistWithId(
        playlistId: String,
        countryCode: String
    ): FetchedResource<MusicSummary.PlaylistSummary, MusifyHttpErrorType>

    suspend fun fetchSearchResultsForQuery(
        searchQuery: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<SearchResults, MusifyHttpErrorType>

    fun fetchAvailableGenres(): List<Genre>

    suspend fun fetchTracksForGenre(
        genre: Genre,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<List<SearchResult.TrackSearchResult>, MusifyHttpErrorType>

    suspend fun fetchTracksForAlbumWithId(
        albumId: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): FetchedResource<List<SearchResult.TrackSearchResult>, MusifyHttpErrorType>

    fun getPaginatedSearchStreamForType(
        paginatedStreamType: PaginatedStreamType,
        searchQuery: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<SearchResult>>

    fun getPaginatedStreamForAlbumsOfArtist(
        artistId: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<SearchResult.AlbumSearchResult>>
}