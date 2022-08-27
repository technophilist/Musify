package com.example.musify.data.repositories.tracksrepository

import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.Genre
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.SearchResult

interface TracksRepository {
    suspend fun fetchTopTenTracksForArtistWithId(
        artistId: String,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<List<SearchResult.TrackSearchResult>, MusifyErrorType>

    suspend fun fetchTracksForGenre(
        genre: Genre,
        imageSize: MapperImageSize,
        countryCode: String
    ): FetchedResource<List<SearchResult.TrackSearchResult>, MusifyErrorType>

    suspend fun fetchTracksForAlbumWithId(
        albumId: String,
        countryCode: String,
        imageSize: MapperImageSize
    ): FetchedResource<List<SearchResult.TrackSearchResult>, MusifyErrorType>
}