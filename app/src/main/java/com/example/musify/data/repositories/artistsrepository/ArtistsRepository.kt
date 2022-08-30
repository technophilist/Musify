package com.example.musify.data.repositories.artistsrepository

import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.SearchResult

/**
 * A repository that contains methods related to artists. **All methods
 * of this interface will always return an instance of [SearchResult.ArtistSearchResult]**
 * encapsulated inside [FetchedResource.Success] if the resource was
 * fetched successfully. This ensures that the return value of all the
 * methods of [ArtistsRepository] will always return [SearchResult.ArtistSearchResult]
 * in the case of a successful fetch operation.
 */
interface ArtistsRepository {
    suspend fun fetchArtistSummaryForId(
        artistId: String,
        imageSize: MapperImageSize
    ): FetchedResource<SearchResult.ArtistSearchResult, MusifyErrorType>

}