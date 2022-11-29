package com.example.musify.data.paging

import com.example.musify.data.remote.response.*

enum class FakeSearchResultsPageNumbers(val pageIndexValue: Int) {
    PAGE_1(0),
    PAGE_2(1),
    PAGE_3(2)
}

private val fakeTracks = List(3) { fakeTrackResponseWithAlbumMetadata.copy(id = it.toString()) }
private val fakeAlbums = List(3) { fakeAlbumMetadataResponse.copy(id = it.toString()) }
private val fakeArtists = List(3) { fakeArtistResponse.copy(id = it.toString()) }
private val fakePlaylists = List(3) { fakePlaylistMetadataResponse.copy(id = it.toString()) }
private val fakeShows = List(3) { fakeShowMetadataResponse.copy(id = it.toString()) }

fun getSearchResultsResponseForFakePageNumber(fakePageNumber: FakeSearchResultsPageNumbers) =
    SearchResultsResponse(
        tracks = SearchResultsResponse.Tracks(listOf(fakeTracks[fakePageNumber.pageIndexValue])),
        albums = SearchResultsResponse.Albums(listOf(fakeAlbums[fakePageNumber.pageIndexValue])),
        artists = SearchResultsResponse.Artists(listOf(fakeArtists[fakePageNumber.pageIndexValue])),
        playlists = SearchResultsResponse.Playlists(listOf(fakePlaylists[fakePageNumber.pageIndexValue])),
        shows = SearchResultsResponse.Shows(listOf(fakeShows[fakePageNumber.pageIndexValue]))
    )
