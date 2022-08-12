package com.example.musify.data.remote.response

val fakePlaylistMetadataResponse = PlaylistMetadataResponse(
    id = "testPlaylistId",
    name = "testPlaylistName",
    images = List(3){fakeImageResponse},
    ownerName = PlaylistResponse.OwnerNameWrapper("testOwnerName")
)