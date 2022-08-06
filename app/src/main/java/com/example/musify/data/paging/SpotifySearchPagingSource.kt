package com.example.musify.data.paging

import com.example.musify.data.response.SearchResultsResponse
import com.example.musify.data.remote.musicservice.SearchQueryType
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.repository.tokenrepository.TokenRepository
import com.example.musify.domain.SearchResult
import retrofit2.HttpException


/**
 * A paging source that is used to get a paginated list of
 * results, of type [T], for the corresponding searchQuery.
 * Note: **The [SearchResultsResponse] parameter provided to the resultsBlock
 * will be based on the searchQueryType**. Only the field corresponding
 * to that [SearchQueryType] will be returned. All other lists would
 * be null.
 *
 * Eg. If [SearchQueryType.TRACK] is passed, all the fields except
 * [SearchResultsResponse.tracks] will be null in the [SearchResultsResponse]
 * instance passed to the resultsBlock.
 */
class SpotifySearchPagingSource<T : SearchResult>(
    searchQuery: String,
    countryCode: String,
    searchQueryType: SearchQueryType,
    tokenRepository: TokenRepository,
    spotifyService: SpotifyService,
    resultsBlock: (SearchResultsResponse) -> List<T>
) : SpotifyPagingSource<T>(
    loadBlock = { limit: Int, offset: Int, prevKey: Int?, nextKey: Int? ->
        try {
            val searchResultsDTO = spotifyService.search(
                searchQuery = searchQuery,
                market = countryCode,
                token = tokenRepository.getValidBearerToken(),
                limit = limit,
                offset = offset,
                type = searchQueryType.value
            )
            val data = resultsBlock(searchResultsDTO)
            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey,
                itemsAfter = data.size
            )
        } catch (httpException: HttpException) {
            LoadResult.Error(httpException)
        }
    }
)