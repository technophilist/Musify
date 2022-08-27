package com.example.musify.data.repositories.artistsrepository

import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.toArtistSearchResult
import com.example.musify.data.repository.tokenrepository.TokenRepository
import com.example.musify.data.repository.tokenrepository.runCatchingWithToken
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.SearchResult
import javax.inject.Inject

class MusifyArtistsRepository @Inject constructor(
    private val spotifyService: SpotifyService,
    private val tokenRepository: TokenRepository
) : ArtistsRepository {

    override suspend fun fetchArtistSummaryForId(
        artistId: String,
        imageSize: MapperImageSize
    ): FetchedResource<SearchResult.ArtistSearchResult, MusifyErrorType> =
        tokenRepository.runCatchingWithToken {
            spotifyService.getArtistInfoWithId(artistId, it).toArtistSearchResult(imageSize)
        }
}