@file:JvmName("FakeSearchResultsKt")

package com.example.musify.data.remote.response

val fakeTrackResponseWithAlbumMetadata = TrackResponseWithAlbumMetadata(
    "testTrackResponseWithAlbumMetadataId",
    "testTrackName",
    "testUrl",
    isPlayable = true,
    explicit = false,
    durationInMillis = 1_000 * 10,
    albumMetadata = fakeAlbumMetadataResponse
)