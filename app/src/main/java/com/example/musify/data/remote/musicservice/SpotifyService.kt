package com.example.musify.data.remote.musicservice

import com.example.musify.data.dto.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface SpotifyService {
    @GET(SpotifyEndPoints.SpecificArtistEndPoint)
    suspend fun getArtistInfoWithId(@Path("id") artistId: String): Response<ArtistDTO>

    @GET(SpotifyEndPoints.AlbumsEndPoint)
    suspend fun getAlbumsOfArtistWithId(
        @Path("id") artistId: String,
        @Query("market") market: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("include_groups") includeGroups: String? = null,
    ): Response<AlbumsMetadataDTO>

    @GET(SpotifyEndPoints.TopTracksEndPoint)
    suspend fun getTopTenTracksForArtistWithId(
        @Path("id") artistId: String,
        @Query("market") market: String
    ): Response<TracksWithAlbumMetadataListDTO>

    @GET(SpotifyEndPoints.SpecificAlbumEndPoint)
    suspend fun getAlbumWithId(
        @Path("id") albumId: String,
        @Query("market") market: String
    ): Response<AlbumDTO>

    @GET(SpotifyEndPoints.SpecificPlaylistEndPoint)
    suspend fun getPlaylistWithId(
        @Path("playlist_id") playlistId: String,
        @Query("market") market: String,
        @Query("fields") fields: String = SpotifyEndPoints.Defaults.defaultPlaylistFields
    ): Response<PlaylistDTO>

    @GET(SpotifyEndPoints.SearchEndPoint)
    suspend fun search(
        @Query("q") searchQuery: String,
        @Query("market") market: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("type") type: String = SpotifyEndPoints.Defaults.defaultSearchQueryTypes,
    ): Response<SearchResultsDTO>
}