package com.example.musify.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.musify.data.paging.*
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.musicservice.SupportedSpotifyGenres
import com.example.musify.data.remote.musicservice.toGenre
import com.example.musify.data.remote.response.*
import com.example.musify.data.remote.token.BearerToken
import com.example.musify.data.repository.tokenrepository.TokenRepository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.*
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import javax.inject.Inject

/**
 * A concrete implementation of [Repository].
 */
class MusifyRepository @Inject constructor(
    private val spotifyService: SpotifyService,
    private val tokenRepository: TokenRepository
) : Repository {
    private suspend fun <R> withToken(block: suspend (BearerToken) -> R): FetchedResource<R, MusifyHttpErrorType> =
        try {
            FetchedResource.Success(block(tokenRepository.getValidBearerToken()))
        } catch (httpException: HttpException) {
            FetchedResource.Failure(httpException.musifyHttpErrorType)
        }

    override suspend fun fetchArtistSummaryForId(
        artistId: String,
        imageSize: MapperImageSize
    ): FetchedResource<MusicSummary.ArtistSummary, MusifyHttpErrorType> = withToken {
        spotifyService.getArtistInfoWithId(artistId, it).toArtistSummary(imageSize)
    }

    override suspend fun fetchAlbumsOfArtistWithId(
        artistId: String,
        imageSize: MapperImageSize,
        countryCode: String, //ISO 3166-1 alpha-2 country code
    ): FetchedResource<List<MusicSummary.AlbumSummary>, MusifyHttpErrorType> = withToken {
        spotifyService.getAlbumsOfArtistWithId(
            artistId,
            countryCode,
            it
        ).toAlbumSummaryList(imageSize)
    }

    override suspend fun fetchTopTenTracksForArtistWithId(
        artistId: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<List<SearchResult.TrackSearchResult>, MusifyHttpErrorType> = withToken {
        spotifyService.getTopTenTracksForArtistWithId(
            artistId = artistId,
            market = countryCode,
            token = it,
        ).value.map { trackDTOWithAlbumMetadata ->
            trackDTOWithAlbumMetadata.toTrackSearchResult(imageSize)
        }
    }

    override suspend fun fetchAlbumWithId(
        albumId: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<MusicSummary.AlbumSummary, MusifyHttpErrorType> = withToken {
        spotifyService.getAlbumWithId(albumId, countryCode, it).toAlbumSummary(imageSize)
    }

    override suspend fun fetchPlaylistWithId(
        playlistId: String,
        countryCode: String
    ): FetchedResource<MusicSummary.PlaylistSummary, MusifyHttpErrorType> = withToken {
        spotifyService.getPlaylistWithId(playlistId, countryCode, it).toPlayListSummary()
    }

    override suspend fun fetchSearchResultsForQuery(
        searchQuery: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<SearchResults, MusifyHttpErrorType> = withToken {
        spotifyService.search(searchQuery, countryCode, it).toSearchResults(imageSize)
    }

    override fun fetchAvailableGenres(): List<Genre> = SupportedSpotifyGenres.values().map {
        it.toGenre()
    }

    override suspend fun fetchTracksForGenre(
        genre: Genre,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<List<SearchResult.TrackSearchResult>, MusifyHttpErrorType> = withToken {
        spotifyService.getTracksForGenre(
            genre = genre.genreType.toSupportedSpotifyGenreType(),
            market = countryCode,
            token = it
        ).value.map { trackDTOWithAlbumMetadata ->
            trackDTOWithAlbumMetadata.toTrackSearchResult(imageSize)
        }
    }

    override fun getPaginatedSearchStreamForType(
        paginatedStreamType: Repository.PaginatedStreamType,
        searchQuery: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<SearchResult>> {
        val pagingSource = when (paginatedStreamType) {
            Repository.PaginatedStreamType.ALBUMS -> SpotifyAlbumSearchPagingSource(
                searchQuery = searchQuery,
                countryCode = countryCode,
                imageSize = imageSize,
                tokenRepository = tokenRepository,
                spotifyService = spotifyService
            )
            Repository.PaginatedStreamType.ARTISTS -> SpotifyArtistSearchPagingSource(
                searchQuery = searchQuery,
                countryCode = countryCode,
                imageSize = imageSize,
                tokenRepository = tokenRepository,
                spotifyService = spotifyService
            )
            Repository.PaginatedStreamType.TRACKS -> SpotifyTrackSearchPagingSource(
                searchQuery = searchQuery,
                countryCode = countryCode,
                imageSize = imageSize,
                tokenRepository = tokenRepository,
                spotifyService = spotifyService
            )
            Repository.PaginatedStreamType.PLAYLISTS -> SpotifyPlaylistSearchPagingSource(
                searchQuery = searchQuery,
                countryCode = countryCode,
                imageSize = imageSize,
                tokenRepository = tokenRepository,
                spotifyService = spotifyService
            )
        }
        return Pager(
            PagingConfig(
                pageSize = SpotifyPagingSource.DEFAULT_PAGE_SIZE,
                initialLoadSize = SpotifyPagingSource.DEFAULT_PAGE_SIZE
            )
        ) { pagingSource }
            .flow as Flow<PagingData<SearchResult>>
    }

}