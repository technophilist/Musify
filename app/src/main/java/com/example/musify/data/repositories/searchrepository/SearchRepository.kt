package com.example.musify.data.repositories.searchrepository

import androidx.paging.PagingData
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.SearchResult
import com.example.musify.domain.SearchResults
import kotlinx.coroutines.flow.Flow

/**
 * A repository that contains all methods related to searching.
 */
interface SearchRepository {
    suspend fun fetchSearchResultsForQuery(
        searchQuery: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<SearchResults, MusifyErrorType>

    fun getPaginatedSearchStreamForAlbums(
        searchQuery: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<SearchResult.AlbumSearchResult>>

    fun getPaginatedSearchStreamForArtists(
        searchQuery: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<SearchResult.ArtistSearchResult>>

    fun getPaginatedSearchStreamForTracks(
        searchQuery: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<SearchResult.TrackSearchResult>>

    fun getPaginatedSearchStreamForPlaylists(
        searchQuery: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<SearchResult.PlaylistSearchResult>>
}