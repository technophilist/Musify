package com.example.musify.data.remote.response

import java.time.LocalDateTime


private val fakeArtistInfoResponse = AlbumMetadataResponse.ArtistInfoResponse(
    "123",
    "test artist"
)

val  fakeAlbumMetadataResponse = AlbumMetadataResponse(
    id = "testId",
    name = "testName",
    albumType = "testAlbumType",
    artists = listOf(fakeArtistInfoResponse),
    images = List(3){ fakeImageResponse},
    releaseDate = LocalDateTime.now().toString(),
    releaseDatePrecision = "",
    totalTracks = 10,
    type = "testType"
)
