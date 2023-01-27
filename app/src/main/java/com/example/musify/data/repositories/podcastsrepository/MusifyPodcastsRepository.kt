package com.example.musify.data.repositories.podcastsrepository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.musify.data.paging.PodcastEpisodesForPodcastShowPagingSource
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
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MusifyPodcastsRepository @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val spotifyService: SpotifyService,
    private val pagingConfig: PagingConfig
) : PodcastsRepository {

    override suspend fun fetchPodcastEpisode(
        episodeId: String,
        countryCode: String,
        imageSize: MapperImageSize,
    ): FetchedResource<PodcastEpisode, MusifyErrorType> = tokenRepository.runCatchingWithToken {
        spotifyService.getEpisodeWithId(
            token = it, id = episodeId, market = countryCode
        ).toPodcastEpisode()
    }

    override suspend fun fetchPodcastShow(
        showId: String, countryCode: String, imageSize: MapperImageSize
    ): FetchedResource<PodcastShow, MusifyErrorType> = tokenRepository.runCatchingWithToken {
        spotifyService.getShowWithId(
            token = it, id = showId, market = countryCode
        ).toPodcastShow()
    }

    override fun getPodcastEpisodesStreamForPodcastShow(
        showId: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<PodcastEpisode>> = Pager(pagingConfig) {
        PodcastEpisodesForPodcastShowPagingSource(
            showId = showId,
            countryCode = countryCode,
            tokenRepository = tokenRepository,
            spotifyService = spotifyService
        )
    }.flow
}
