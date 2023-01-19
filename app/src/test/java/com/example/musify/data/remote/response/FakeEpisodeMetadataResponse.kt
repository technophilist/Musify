package com.example.musify.data.remote.response

private const val oneMinute = 1_000L * 60
private const val oneHour = oneMinute * 60
val fakeEpisodeMetadataResponse = EpisodeMetadataResponse(
    id = "FakeEpisodeMetadata",
    title = "Title",
    "Lorem Ipsum".repeat(10),
    durationMillis = (3 * oneHour) + (24 * oneMinute), // 3hrs 24 minutes
    images = List(3) { fakeImageResponse },
    releaseDate = "2022-12-02"
)