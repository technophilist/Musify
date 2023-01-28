package com.example.musify.data.repositories.albumsrepository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.musify.data.paging.AlbumsOfArtistPagingSource
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.toAlbumSearchResult
import com.example.musify.data.remote.response.toAlbumSearchResultList
import com.example.musify.data.repositories.tokenrepository.TokenRepository
import com.example.musify.data.repositories.tokenrepository.runCatchingWithToken
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.SearchResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MusifyAlbumsRepository @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val spotifyService: SpotifyService,
    private val pagingConfig: PagingConfig
) : AlbumsRepository {

    override suspend fun fetchAlbumsOfArtistWithId(
        artistId: String,
        imageSize: MapperImageSize,
        countryCode: String, //ISO 3166-1 alpha-2 country code
    ): FetchedResource<List<SearchResult.AlbumSearchResult>, MusifyErrorType> =
        tokenRepository.runCatchingWithToken {
            spotifyService.getAlbumsOfArtistWithId(
                artistId,
                countryCode,
                it
            ).toAlbumSearchResultList()
        }

    override suspend fun fetchAlbumWithId(
        albumId: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<SearchResult.AlbumSearchResult, MusifyErrorType> =
        tokenRepository.runCatchingWithToken {
            spotifyService.getAlbumWithId(albumId, countryCode, it).toAlbumSearchResult()
        }

    override fun getPaginatedStreamForAlbumsOfArtist(
        artistId: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<SearchResult.AlbumSearchResult>> = Pager(pagingConfig) {
        AlbumsOfArtistPagingSource(
            artistId = artistId,
            market = countryCode,
            mapperImageSize = imageSize,
            tokenRepository = tokenRepository,
            spotifyService = spotifyService
        )
    }.flow
}