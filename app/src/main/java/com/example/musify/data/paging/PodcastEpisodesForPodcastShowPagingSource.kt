package com.example.musify.data.paging

import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.toPodcastEpisode
import com.example.musify.data.repositories.tokenrepository.TokenRepository
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.PodcastEpisode
import retrofit2.HttpException
import java.io.IOException

class PodcastEpisodesForPodcastShowPagingSource(
    showId: String,
    countryCode: String,
    imageSize: MapperImageSize,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService
) : SpotifyPagingSource<PodcastEpisode>(
    loadBlock = { limit, offset ->
        try {
            val showResponse = spotifyService.getShowWithId(
                token = tokenRepository.getValidBearerToken(),
                id = showId,
                market = countryCode,
            )
            val episodes = spotifyService.getEpisodesForShowWithId(
                token = tokenRepository.getValidBearerToken(),
                id = showId,
                market = countryCode,
                limit = limit,
                offset = offset
            )
                .items
                .map {
                    it.toPodcastEpisode(
                        imageSizeForPodcastShowImage = imageSize,
                        showResponse = showResponse
                    )
                }
            SpotifyLoadResult.PageData(episodes)
        } catch (httpException: HttpException) {
            SpotifyLoadResult.Error(httpException)
        } catch (ioException: IOException) {
            SpotifyLoadResult.Error(ioException)
        }
    }
)