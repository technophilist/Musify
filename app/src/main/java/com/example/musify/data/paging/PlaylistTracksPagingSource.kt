package com.example.musify.data.paging

import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.toTrackSearchResult
import com.example.musify.data.repositories.tokenrepository.TokenRepository
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.SearchResult
import retrofit2.HttpException
import java.io.IOException

class PlaylistTracksPagingSource(
    playlistId: String,
    countryCode: String,
    imageSize: MapperImageSize,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService
) : SpotifyPagingSource<SearchResult.TrackSearchResult>(
    loadBlock = { limit, offset ->
        try {
            val data = spotifyService.getTracksForPlaylist(
                playlistId = playlistId,
                market = countryCode,
                token = tokenRepository.getValidBearerToken(),
                limit = limit,
                offset = offset
            ).items.map { it.track.toTrackSearchResult(imageSize) }
            SpotifyLoadResult.PageData(data)
        } catch (httpException: HttpException) {
            SpotifyLoadResult.Error(httpException)
        } catch (ioException: IOException) {
            SpotifyLoadResult.Error(ioException)
        }
    }
)