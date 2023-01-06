package com.example.musify.data.repositories.podcastsrepository

import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.toPodcastEpisode
import com.example.musify.data.remote.response.toPodcastShow
import com.example.musify.data.repositories.tokenrepository.TokenRepository
import com.example.musify.data.repositories.tokenrepository.runCatchingWithToken
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.PodcastEpisode
import com.example.musify.domain.PodcastShow
import javax.inject.Inject

class MusifyPodcastsRepository @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val spotifyService: SpotifyService
) : PodcastsRepository {

    override suspend fun fetchPodcastEpisode(
        episodeId: String,
        countryCode: String,
        imageSize: MapperImageSize,
    ): FetchedResource<PodcastEpisode, MusifyErrorType> = tokenRepository.runCatchingWithToken {
        spotifyService.getEpisodeWithId(
            token = it,
            id = episodeId,
            market = countryCode
        ).toPodcastEpisode(imageSize)
    }

    override suspend fun fetchPodcastShow(
        showId: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): FetchedResource<PodcastShow, MusifyErrorType> = tokenRepository.runCatchingWithToken {
        spotifyService.getShowWithId(
            token = it,
            id = showId,
            market = countryCode
        ).toPodcastShow(imageSize)
    }
}