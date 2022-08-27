package com.example.musify.data.repositories.artistsrepository

import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.SearchResult

interface ArtistsRepository {
    suspend fun fetchArtistSummaryForId(
        artistId: String,
        imageSize: MapperImageSize
    ): FetchedResource<SearchResult.ArtistSearchResult, MusifyErrorType>

}