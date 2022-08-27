package com.example.musify.data.repositories.albumsrepository

import androidx.paging.PagingData
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.SearchResult
import kotlinx.coroutines.flow.Flow

interface AlbumsRepository {
    suspend fun fetchAlbumWithId(
        albumId: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<SearchResult.AlbumSearchResult, MusifyErrorType>

    suspend fun fetchAlbumsOfArtistWithId(
        artistId: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<List<SearchResult.AlbumSearchResult>, MusifyErrorType>

    fun getPaginatedStreamForAlbumsOfArtist(
        artistId: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<SearchResult.AlbumSearchResult>>
}
