package com.example.musify.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * A sealed class that contains the logic to manage keys for a paginated
 * stream of type [V], from the Spotify API. The [loadBlock] can be used
 * to define what is to be loaded. The lambda will be provided with the
 * limit, offset, token, previous and next keys. The caller just needs to
 * define how to fetch the required type, taking care of handling any
 * exceptions.
 */
sealed class SpotifyPagingSource<V : Any>(
    private val loadBlock: suspend (
        limit: Int,
        offset: Int,
        prevKey: Int?,
        nextKey: Int?
    ) -> LoadResult<Int, V>
) : PagingSource<Int, V>() {
    private var isLastPage: Boolean = false

    override fun getRefreshKey(state: PagingState<Int, V>): Int? = state.anchorPosition
        ?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, V> {
        val pageNumber = params.key ?: 0
        val previousKey = if (pageNumber == 0) null else pageNumber - 1
        val nextKey = if (isLastPage) null else pageNumber + 1
        val loadResult = loadBlock(
            params.loadSize.coerceAtMost(50), // Spotify API doesn't allow 'limit' to exceed 50
            params.loadSize * pageNumber,
            previousKey,
            nextKey
        )
        if (loadResult is LoadResult.Page) isLastPage = loadResult.itemsAfter == 0
        return loadResult
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}