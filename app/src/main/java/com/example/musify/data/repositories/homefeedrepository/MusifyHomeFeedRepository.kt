package com.example.musify.data.repositories.homefeedrepository

import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.toAlbumSearchResultList
import com.example.musify.data.remote.response.toFeaturedPlaylists
import com.example.musify.data.remote.response.toPlaylistSearchResultList
import com.example.musify.data.repositories.tokenrepository.TokenRepository
import com.example.musify.data.repositories.tokenrepository.runCatchingWithToken
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.FeaturedPlaylists
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.PlaylistsForCategory
import com.example.musify.domain.SearchResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class MusifyHomeFeedRepository @Inject constructor(
    private val spotifyService: SpotifyService,
    private val tokenRepository: TokenRepository
) : HomeFeedRepository {
    override suspend fun fetchNewlyReleasedAlbums(
        countryCode: String,
        mapperImageSize: MapperImageSize
    ): FetchedResource<List<SearchResult.AlbumSearchResult>, MusifyErrorType> =
        tokenRepository.runCatchingWithToken { token ->
            spotifyService
                .getNewReleases(token = token, market = countryCode)
                .toAlbumSearchResultList()
        }


    override suspend fun fetchFeaturedPlaylistsForCurrentTimeStamp(
        timestampMillis: Long,
        countryCode: String,
        languageCode: ISO6391LanguageCode,
    ): FetchedResource<FeaturedPlaylists, MusifyErrorType> =
        tokenRepository.runCatchingWithToken { token ->
            val timestamp = ISODateTimeString.from(timestampMillis)
            spotifyService.getFeaturedPlaylists(
                token = token,
                market = countryCode,
                locale = "${languageCode.value}_$countryCode",
                timestamp = timestamp
            ).toFeaturedPlaylists()
        }

    override suspend fun fetchPlaylistsBasedOnCategoriesAvailableForCountry(
        countryCode: String,
        languageCode: ISO6391LanguageCode,
    ): FetchedResource<List<PlaylistsForCategory>, MusifyErrorType> =
        tokenRepository.runCatchingWithToken { token ->
            val locale = "${languageCode.value}_$countryCode"
            val categories = spotifyService.getBrowseCategories(
                token = token,
                market = countryCode,
                locale = locale
            ).categories.items
            coroutineScope {
                // instead of fetching playlists for each category in a sequential manner
                // fetch it in parallel
                val playlistsMap = categories.map { huh ->
                    async {
                        spotifyService.getPlaylistsForCategory(
                            token = token,
                            categoryId = huh.id,
                            market = countryCode
                        ).toPlaylistSearchResultList()
                    }
                }
                playlistsMap.awaitAll().mapIndexed { index, playlists ->
                    PlaylistsForCategory(
                        categoryId = categories[index].id,
                        nameOfCategory = categories[index].name,
                        associatedPlaylists = playlists
                    )
                }
            }
        }
}
