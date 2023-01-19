package com.example.musify.data.paging

import androidx.paging.PagingSource
import com.example.musify.data.remote.musicservice.SearchQueryType
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.response.SearchResultsResponse
import com.example.musify.data.remote.response.toSearchResults
import com.example.musify.data.repositories.tokenrepository.TokenRepositoryStub
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.SearchResult
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import java.io.IOException


private const val TEST_PAGE_SIZE = 2
private const val PAGE_NUMBER_THAT_THROWS_IO_EXCEPTION = 100


class SpotifySearchPagingSourceTest {
    private lateinit var pagingSource: SpotifyPagingSource<SearchResult.TrackSearchResult>

    @Before
    fun setUp() {
        val spotifyServiceStub = mock<SpotifyService> {
            onBlocking { search(any(), any(), any(), any(), any(), any()) } doAnswer {
                when (it.arguments[4]) {
                    PAGE_NUMBER_THAT_THROWS_IO_EXCEPTION * TEST_PAGE_SIZE -> throw IOException("")
                    0 -> getSearchResultsResponseForFakePageNumber(FakeSearchResultsPageNumbers.PAGE_1)
                    2 -> getSearchResultsResponseForFakePageNumber(FakeSearchResultsPageNumbers.PAGE_2)
                    4 -> getSearchResultsResponseForFakePageNumber(FakeSearchResultsPageNumbers.PAGE_3)
                    else -> SearchResultsResponse(null, null, null, null, null, null) // last page
                }
            }
        }
        pagingSource = SpotifySearchPagingSource(
            searchQuery = "",
            countryCode = "",
            tokenRepository = TokenRepositoryStub(),
            searchQueryType = SearchQueryType.TRACK,
            spotifyService = spotifyServiceStub,
            resultsBlock = { it.toSearchResults(MapperImageSize.SMALL).tracks }
        )
    }

    @Test
    fun getFirstPageTest_attemptToFetchFirstPage_returnsValidPages() {
        val loadParams = PagingSource.LoadParams.Refresh(
            key = 0,
            loadSize = TEST_PAGE_SIZE,
            placeholdersEnabled = false
        )
        val expectedLoadResult = PagingSource.LoadResult.Page(
            data = getSearchResultsResponseForFakePageNumber(FakeSearchResultsPageNumbers.PAGE_1)
                .toSearchResults(MapperImageSize.SMALL).tracks,
            prevKey = null,
            nextKey = 1
        )
        val loadResult = runBlocking { pagingSource.load(loadParams) }
        assert(loadResult is PagingSource.LoadResult.Page)
        loadResult as PagingSource.LoadResult.Page
        assert(loadResult == expectedLoadResult)
    }

    @Test
    fun getPagesTest_attemptToFetchAllPages_returnsValidPages() {
        FakeSearchResultsPageNumbers.values().forEachIndexed { index, fakeSearchPageNumber ->
            val loadParams = PagingSource.LoadParams.Refresh(
                key = if (index == 0) null else index,
                loadSize = TEST_PAGE_SIZE,
                placeholdersEnabled = false
            )
            val expectedLoadResult = PagingSource.LoadResult.Page(
                data = getSearchResultsResponseForFakePageNumber(fakeSearchPageNumber)
                    .toSearchResults(MapperImageSize.SMALL).tracks,
                prevKey = if (index == 0) null else index - 1,
                nextKey = index + 1
            )
            val loadResult = runBlocking { pagingSource.load(loadParams) }
            assert(loadResult is PagingSource.LoadResult.Page)
            assert(loadResult == expectedLoadResult)
        }
    }

    @Test
    fun getLastPageTest_attemptToFetchLastPage_returnsValidPageWithNextKeySetToNull() {
        val loadParams = PagingSource.LoadParams.Refresh(
            key = 3,
            loadSize = TEST_PAGE_SIZE,
            placeholdersEnabled = false
        )
        val expectedLoadResult = PagingSource.LoadResult.Page(
            data = SearchResultsResponse(null, null, null, null, null,null)
                .toSearchResults(MapperImageSize.SMALL).tracks,
            prevKey = 2,
            nextKey = null
        )
        val loadResult = runBlocking { pagingSource.load(loadParams) }
        assert(loadResult is PagingSource.LoadResult.Page)
        loadResult as PagingSource.LoadResult.Page
        assert(loadResult == expectedLoadResult)
    }

    @Test
    fun exceptionHandlingTest_attemptToFetchPageThatThrowsException_returnErrorObject() {
        val loadParams = PagingSource.LoadParams.Refresh(
            key = PAGE_NUMBER_THAT_THROWS_IO_EXCEPTION,
            loadSize = TEST_PAGE_SIZE,
            placeholdersEnabled = false
        )
        val loadResult = runBlocking { pagingSource.load(loadParams) }
        assert(loadResult is PagingSource.LoadResult.Error<Int, SearchResult.TrackSearchResult>)
        loadResult as PagingSource.LoadResult.Error
        assert(loadResult.throwable is IOException)
    }
}