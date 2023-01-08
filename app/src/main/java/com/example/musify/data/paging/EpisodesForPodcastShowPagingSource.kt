package com.example.musify.data.paging

import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.toStreamableEpisodeSearchResult
import com.example.musify.data.repositories.tokenrepository.TokenRepository
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.SearchResult
import okio.IOException
import retrofit2.HttpException

class EpisodesForPodcastShowPagingSource(
    showId: String,
    countryCode: String,
    imageSize: MapperImageSize,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService
) : SpotifyPagingSource<SearchResult.StreamableEpisodeSearchResult>(
    loadBlock = { limit, offset ->
        try {
            val episodes = spotifyService.getEpisodesForShowWithId(
                token = tokenRepository.getValidBearerToken(),
                id = showId,
                market = countryCode,
                limit = limit,
                offset = offset
            ).items.map { it.toStreamableEpisodeSearchResult(imageSize) }
            SpotifyLoadResult.PageData(episodes)
        } catch (httpException: HttpException) {
            SpotifyLoadResult.Error(httpException)
        } catch (ioException: IOException) {
            SpotifyLoadResult.Error(ioException)
        }
    }
)
