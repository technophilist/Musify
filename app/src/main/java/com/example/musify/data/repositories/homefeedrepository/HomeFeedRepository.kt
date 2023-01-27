package com.example.musify.data.repositories.homefeedrepository

import com.example.musify.data.utils.FetchedResource
import com.example.musify.domain.FeaturedPlaylists
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.PlaylistsForCategory
import com.example.musify.domain.SearchResult

/**
 * An interface that contains the requisite methods required for a repository
 * that contains methods for fetching items to be displayed in the home
 * feed.
 */
interface HomeFeedRepository {
    suspend fun fetchFeaturedPlaylistsForCurrentTimeStamp(
        timestampMillis: Long,
        countryCode: String,
        languageCode: ISO6391LanguageCode,
    ): FetchedResource<FeaturedPlaylists, MusifyErrorType>

    suspend fun fetchPlaylistsBasedOnCategoriesAvailableForCountry(
        countryCode: String,
        languageCode: ISO6391LanguageCode,
    ): FetchedResource<List<PlaylistsForCategory>, MusifyErrorType>

    suspend fun fetchNewlyReleasedAlbums(
        countryCode: String
    ): FetchedResource<List<SearchResult.AlbumSearchResult>, MusifyErrorType>
}