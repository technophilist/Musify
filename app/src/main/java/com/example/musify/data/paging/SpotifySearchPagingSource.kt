package com.example.musify.data.paging

import com.example.musify.data.remote.musicservice.SearchQueryType
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.SearchResultsResponse
import com.example.musify.data.repositories.tokenrepository.TokenRepository
import com.example.musify.domain.SearchResult
import retrofit2.HttpException
import java.io.IOException


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
    loadBlock = { limit: Int, offset: Int ->
        try {
            val searchResultsResponse = spotifyService.search(
                searchQuery = searchQuery,
                market = countryCode,
                token = tokenRepository.getValidBearerToken(),
                limit = limit,
                offset = offset,
                type = searchQueryType.value
            )
            SpotifyLoadResult.PageData(resultsBlock(searchResultsResponse))
        } catch (httpException: HttpException) {
            SpotifyLoadResult.Error(httpException)
        } catch (ioException: IOException) {
            // indicates that there was some network error
            SpotifyLoadResult.Error(ioException)
        }
    }
)

/**
 * A load result sealed class that is associated with [SpotifySearchPagingSource].
 */
sealed class SpotifyLoadResult<Value : Any> {
    /**
     * A class that models a successful load with the specified [data]
     * of type [Value].
     */
    data class PageData<Value : Any>(val data: List<Value>) : SpotifyLoadResult<Value>()

    /**
     * A class that models an error that happened during a load. It contains
     * a [throwable] that specifies the exception that occured.
     */
    data class Error<Value : Any>(val throwable: Throwable) : SpotifyLoadResult<Value>()
}
