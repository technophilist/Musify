package com.example.musify.ui.screens.searchscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.example.musify.domain.SearchResult
import com.example.musify.ui.components.ListItemCardType
import com.example.musify.ui.components.MusifyCompactListItemCard
import com.example.musify.ui.components.MusifyCompactTrackCard
import com.example.musify.ui.components.MusifyCompactTrackCardDefaults
import com.google.accompanist.insets.imePadding

@ExperimentalMaterialApi
fun LazyListScope.searchTrackListItems(
    tracksListForSearchQuery: LazyPagingItems<SearchResult.TrackSearchResult>,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    onItemClick: (SearchResult) -> Unit,
    isLoadingPlaceholderVisible: (SearchResult.TrackSearchResult) -> Boolean,
    onImageLoading: (SearchResult) -> Unit,
    onImageLoadingFinished: (SearchResult.TrackSearchResult, Throwable?) -> Unit
) {
    itemsIndexedWithEmptyListContent(
        items = tracksListForSearchQuery,
        key = { index, track -> "$index${track.id}" }
    ) { _, track ->
        track?.let {
            MusifyCompactTrackCard(
                track = it,
                onClick = onItemClick,
                isLoadingPlaceholderVisible = isLoadingPlaceholderVisible(it),
                onImageLoading = onImageLoading,
                onImageLoadingFinished = onImageLoadingFinished,
                isCurrentlyPlaying = it == currentlyPlayingTrack
            )
        }
    }
}

@ExperimentalMaterialApi
fun LazyListScope.searchAlbumListItems(
    albumListForSearchQuery: LazyPagingItems<SearchResult.AlbumSearchResult>,
    onItemClick: (SearchResult) -> Unit,
    isLoadingPlaceholderVisible: (SearchResult.AlbumSearchResult) -> Boolean,
    onImageLoading: (SearchResult) -> Unit,
    onImageLoadingFinished: (SearchResult.AlbumSearchResult, Throwable?) -> Unit
) {

    itemsIndexedWithEmptyListContent(
        items = albumListForSearchQuery,
        key = { index, album -> "$index${album.id}" }
    ) { _, album ->
        album?.let {
            MusifyCompactListItemCard(
                cardType = ListItemCardType.ALBUM,
                thumbnailImageUrlString = it.albumArtUrlString,
                title = it.name,
                subtitle = it.artistsString,
                onClick = { onItemClick(it) },
                onTrailingButtonIconClick = { /**/ },
                isLoadingPlaceHolderVisible = isLoadingPlaceholderVisible(it),
                onThumbnailImageLoadingFinished = { throwable ->
                    onImageLoadingFinished(it, throwable)
                },
                onThumbnailLoading = { onImageLoading(it) },
                contentPadding = MusifyCompactTrackCardDefaults.defaultContentPadding
            )
        }
    }
}

@ExperimentalMaterialApi
fun LazyListScope.searchArtistListItems(
    artistListForSearchQuery: LazyPagingItems<SearchResult.ArtistSearchResult>,
    onItemClick: (SearchResult) -> Unit,
    isLoadingPlaceholderVisible: (SearchResult.ArtistSearchResult) -> Boolean,
    onImageLoading: (SearchResult) -> Unit,
    onImageLoadingFinished: (SearchResult.ArtistSearchResult, Throwable?) -> Unit,
    artistImageErrorPainter: Painter
) {
    itemsIndexedWithEmptyListContent(
        items = artistListForSearchQuery,
        key = { index, artist -> "$index${artist.id}" }
    ) { _, artist ->
        artist?.let {
            MusifyCompactListItemCard(
                cardType = ListItemCardType.ARTIST,
                thumbnailImageUrlString = it.imageUrlString ?: "",
                title = it.name,
                subtitle = "Artist",
                onClick = { onItemClick(it) },
                onTrailingButtonIconClick = { /*TODO*/ },
                isLoadingPlaceHolderVisible = isLoadingPlaceholderVisible(it),
                onThumbnailImageLoadingFinished = { throwable ->
                    onImageLoadingFinished(it, throwable)
                },
                onThumbnailLoading = { onImageLoading(it) },
                errorPainter = artistImageErrorPainter,
                contentPadding = MusifyCompactTrackCardDefaults.defaultContentPadding
            )
        }
    }
}

@ExperimentalMaterialApi
fun LazyListScope.searchPlaylistListItems(
    playlistListForSearchQuery: LazyPagingItems<SearchResult.PlaylistSearchResult>,
    onItemClick: (SearchResult) -> Unit,
    isLoadingPlaceholderVisible: (SearchResult.PlaylistSearchResult) -> Boolean,
    onImageLoading: (SearchResult) -> Unit,
    onImageLoadingFinished: (SearchResult.PlaylistSearchResult, Throwable?) -> Unit,
    playlistImageErrorPainter: Painter
) {
    itemsIndexedWithEmptyListContent(
        items = playlistListForSearchQuery,
        key = { index, playlist -> "$index${playlist.id}" }
    ) { _, playlist ->
        playlist?.let {
            MusifyCompactListItemCard(
                cardType = ListItemCardType.PLAYLIST,
                thumbnailImageUrlString = it.imageUrlString ?: "",
                title = it.name,
                subtitle = "Playlist",
                onClick = { onItemClick(it) },
                onTrailingButtonIconClick = { /* TODO*/ },
                isLoadingPlaceHolderVisible = isLoadingPlaceholderVisible(it),
                onThumbnailImageLoadingFinished = { throwable ->
                    onImageLoadingFinished(it, throwable)
                },
                onThumbnailLoading = { onImageLoading(it) },
                errorPainter = playlistImageErrorPainter,
                contentPadding = MusifyCompactTrackCardDefaults.defaultContentPadding
            )
        }
    }
}

@Composable
private fun DefaultEmptyListContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Couldn't find anything matching the search query",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Try searching again using a different spelling or keyword.",
            style = LocalTextStyle.current.copy(
                color = Color.White.copy(alpha = ContentAlpha.disabled)
            ),
            textAlign = TextAlign.Center
        )
    }
}

private fun <T : Any> LazyListScope.itemsIndexedWithEmptyListContent(
    items: LazyPagingItems<T>,
    key: ((index: Int, item: T) -> Any)? = null,
    emptyListContent: @Composable LazyItemScope.() -> Unit = {
        DefaultEmptyListContent(
            modifier = Modifier
                .fillParentMaxSize()
                .padding(horizontal = 16.dp)
                .imePadding()
        )
    },
    itemContent: @Composable LazyItemScope.(index: Int, value: T?) -> Unit
) {
    // items.loadState.append.endOfPaginationReached && items.itemCount == 0
    if (true) {
        item { emptyListContent.invoke(this) }
    } else {
        itemsIndexed(items, key, itemContent)
    }
}
