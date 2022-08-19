package com.example.musify.ui.screens.searchscreen

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.graphics.painter.Painter
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.example.musify.domain.SearchResult
import com.example.musify.ui.components.ListItemCardType
import com.example.musify.ui.components.MusifyCompactListItemCard
import com.example.musify.ui.components.MusifyCompactTrackCard

@ExperimentalMaterialApi
fun LazyListScope.searchTrackListItems(
    tracksListForSearchQuery: LazyPagingItems<SearchResult.TrackSearchResult>,
    currentlyPlayingTrack: SearchResult.TrackSearchResult?,
    onItemClick: (SearchResult) -> Unit,
    isLoadingPlaceholderVisible: (SearchResult.TrackSearchResult) -> Boolean,
    onImageLoading: (SearchResult) -> Unit,
    onImageLoadingFinished: (SearchResult.TrackSearchResult, Throwable?) -> Unit
) {
    itemsIndexed(
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

    itemsIndexed(
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
    itemsIndexed(
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
    itemsIndexed(
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
                errorPainter = playlistImageErrorPainter
            )
        }
    }
}