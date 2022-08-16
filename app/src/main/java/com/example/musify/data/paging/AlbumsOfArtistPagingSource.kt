package com.example.musify.data.paging

import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.toAlbumSearchResult
import com.example.musify.data.repository.tokenrepository.TokenRepository
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.SearchResult
import retrofit2.HttpException
import java.io.IOException

class AlbumsOfArtistPagingSource(
    private val artistId: String,
    private val market: String,
    private val mapperImageSize: MapperImageSize,
    private val tokenRepository: TokenRepository,
    private val spotifyService: SpotifyService,
) : SpotifyPagingSource<SearchResult.AlbumSearchResult>(
    loadBlock = { limit, offset ->
        try {
            val albumsMetadataResponse = spotifyService.getAlbumsOfArtistWithId(
                artistId = artistId,
                market = market,
                token = tokenRepository.getValidBearerToken(),
                limit = limit,
                offset = offset,
            )
            val data = albumsMetadataResponse.items.map { it.toAlbumSearchResult(mapperImageSize) }
            SpotifyLoadResult.PageData(data)
        } catch (httpException: HttpException) {
            SpotifyLoadResult.Error(httpException)
        } catch (ioException: IOException) {
            SpotifyLoadResult.Error(ioException)
        }
    }
)