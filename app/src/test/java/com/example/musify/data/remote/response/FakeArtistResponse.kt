package com.example.musify.data.remote.response

private val fakeArtistFollowers = ArtistResponse.Followers("10")

val fakeArtistResponse = ArtistResponse(
    id = "testArtistId",
    name = "testArtistName",
    images = List(3) { fakeImageResponse },
    followers = fakeArtistFollowers,
)

