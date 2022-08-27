package com.example.musify.data.repositories.tracksrepository

import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.getTracks
import com.example.musify.data.remote.response.toTrackSearchResult
import com.example.musify.data.repositories.tokenrepository.TokenRepository
import com.example.musify.data.repositories.tokenrepository.runCatchingWithToken
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.Genre
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.SearchResult
import com.example.musify.domain.toSupportedSpotifyGenreType
import javax.inject.Inject

class MusifyTracksRepository @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val spotifyService: SpotifyService
) : TracksRepository {
    override suspend fun fetchTopTenTracksForArtistWithId(
        artistId: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<List<SearchResult.TrackSearchResult>, MusifyErrorType> =
        tokenRepository.runCatchingWithToken {
            spotifyService.getTopTenTracksForArtistWithId(
                artistId = artistId,
                market = countryCode,
                token = it,
            ).value.map { trackDTOWithAlbumMetadata ->
                trackDTOWithAlbumMetadata.toTrackSearchResult(imageSize)
            }
        }

    override suspend fun fetchTracksForGenre(
        genre: Genre,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<List<SearchResult.TrackSearchResult>, MusifyErrorType> =
        tokenRepository.runCatchingWithToken {
            spotifyService.getTracksForGenre(
                genre = genre.genreType.toSupportedSpotifyGenreType(),
                market = countryCode,
                token = it
            ).value.map { trackDTOWithAlbumMetadata ->
                trackDTOWithAlbumMetadata.toTrackSearchResult(imageSize)
            }
        }

    override suspend fun fetchTracksForAlbumWithId(
        albumId: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): FetchedResource<List<SearchResult.TrackSearchResult>, MusifyErrorType> =
        tokenRepository.runCatchingWithToken {
            spotifyService.getAlbumWithId(albumId, countryCode, it).getTracks(imageSize)
        }
}