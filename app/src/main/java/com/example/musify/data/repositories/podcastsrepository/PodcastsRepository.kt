package com.example.musify.data.repositories.podcastsrepository

import androidx.paging.PagingData
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.PodcastEpisode
import com.example.musify.domain.PodcastShow
import com.example.musify.domain.SearchResult
import kotlinx.coroutines.flow.Flow

/**
 * A repository that contains all methods related to podcasts.
 */
interface PodcastsRepository {
    suspend fun fetchPodcastEpisode(
        episodeId: String,
        countryCode: String,
        imageSize: MapperImageSize,
    ): FetchedResource<PodcastEpisode, MusifyErrorType>

    suspend fun fetchPodcastShow(
        showId: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): FetchedResource<PodcastShow, MusifyErrorType>

    @Deprecated("Use getPodcastEpisodesStreamForPodcastShow")
    fun getEpisodesStreamForPodcastShow(
        showId: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<SearchResult.StreamableEpisodeSearchResult>>

    fun getPodcastEpisodesStreamForPodcastShow(
        showId: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): Flow<PagingData<PodcastEpisode>>
}