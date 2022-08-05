package com.example.musify.ui.screens.searchscreen

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.graphics.painter.Painter
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.example.musify.domain.SearchResult
import com.example.musify.ui.components.ListItemCardType
import com.example.musify.ui.components.MusifyCompactListItemCard

@ExperimentalMaterialApi
fun LazyListScope.searchTrackListItems(
    tracksListForSearchQuery: LazyPagingItems<SearchResult.TrackSearchResult>,
    onItemClick: (SearchResult) -> Unit,
    isLoadingPlaceholderVisible: (SearchResult.TrackSearchResult) -> Boolean,
    onImageLoading: (SearchResult) -> Unit,
    onImageLoadingFinished: (SearchResult.TrackSearchResult, Throwable?) -> Unit
) {
    items(tracksListForSearchQuery, key = { it.id }) {
        it?.let {
            MusifyCompactListItemCard(
                cardType = ListItemCardType.TRACK,
                thumbnailImageUrlString = it.imageUrlString,
                title = it.name,
                subtitle = it.artistsString,
                onClick = { onItemClick(it) },
                onTrailingButtonIconClick = { /* TODO*/ },
                isLoadingPlaceHolderVisible = isLoadingPlaceholderVisible(it),
                onThumbnailImageLoadingFinished = { throwable ->
                    onImageLoadingFinished(it, throwable)
                },
                onThumbnailLoading = { onImageLoading(it) }
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

    items(albumListForSearchQuery, key = { it.id }) {
        it?.let {
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
                onThumbnailLoading = { onImageLoading(it) }
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
    items(artistListForSearchQuery, key = { it.id }) {
        it?.let {
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
                errorPainter = artistImageErrorPainter
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
    items(playlistListForSearchQuery, key = { it.id }) {
        it?.let {
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
                errorPainter = playlistImageErrorPainter
            )
        }
    }
}

@ExperimentalMaterialApi
fun LazyListScope.allItems(
    albumListForSearchQuery: LazyPagingItems<SearchResult.AlbumSearchResult>,
    artistListForSearchQuery: LazyPagingItems<SearchResult.ArtistSearchResult>,
    tracksListForSearchQuery: LazyPagingItems<SearchResult.TrackSearchResult>,
    playlistListForSearchQuery: LazyPagingItems<SearchResult.PlaylistSearchResult>,
    onItemClick: (SearchResult) -> Unit,
    isLoadingPlaceholderVisible: (SearchResult) -> Boolean,
    onImageLoading: (SearchResult) -> Unit,
    onImageLoadingFinished: (SearchResult, Throwable?) -> Unit,
    artistImageErrorPainter:Painter,
    playlistImageErrorPainter: Painter
) {
    searchAlbumListItems(
        albumListForSearchQuery = albumListForSearchQuery,
        onItemClick = onItemClick,
        isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
        onImageLoading = onImageLoading,
        onImageLoadingFinished = onImageLoadingFinished
    )
    searchTrackListItems(
        tracksListForSearchQuery = tracksListForSearchQuery,
        onItemClick = onItemClick,
        isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
        onImageLoading = onImageLoading,
        onImageLoadingFinished = onImageLoadingFinished
    )
    searchArtistListItems(
        artistListForSearchQuery = artistListForSearchQuery,
        onItemClick = onItemClick,
        isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
        onImageLoading = onImageLoading,
        onImageLoadingFinished = onImageLoadingFinished,
        artistImageErrorPainter = artistImageErrorPainter
    )
    searchPlaylistListItems(
        playlistListForSearchQuery = playlistListForSearchQuery,
        onItemClick = onItemClick,
        isLoadingPlaceholderVisible = isLoadingPlaceholderVisible,
        onImageLoading = onImageLoading,
        onImageLoadingFinished = onImageLoadingFinished,
        playlistImageErrorPainter = playlistImageErrorPainter
    )
}