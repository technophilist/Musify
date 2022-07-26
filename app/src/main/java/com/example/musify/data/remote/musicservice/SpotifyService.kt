package com.example.musify.data.remote.musicservice

import com.example.musify.data.dto.*
import com.example.musify.data.remote.token.BearerToken
import com.example.musify.domain.Genre
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * An enum that contains all the generes supported by the spotify api.
 * Note: It is not an exhaustive collection of all the genres supported
 * by the spotify api.
 * @param queryStringValue depicts the actual string value that will be
 * appended to the query when the GET request is made. This value will
 * be the returned when [SupportedSpotifyGenres.toString] is called.
 */
enum class SupportedSpotifyGenres(private val queryStringValue: String) {
    AMBIENT("ambient"),
    CHILL("chill"),
    CLASSICAL("classical"),
    DANCE("dance"),
    ELECTRONIC("electronic"),
    METAL("metal"),
    RAINY_DAY("rainy-day"),
    ROCK("rock"),
    PIANO("piano"),
    POP("pop"),
    SLEEP("sleep");

    override fun toString() = queryStringValue

}

/**
 * A mapper function used to map an enum of type [SupportedSpotifyGenres]
 * to an enum of type [Genre].
 */
fun SupportedSpotifyGenres.toGenre(): Genre {
    val genreType = getGenreType()
    val name = when (genreType) {
        Genre.GenreType.AMBIENT -> "Ambient"
        Genre.GenreType.CHILL -> "Chill"
        Genre.GenreType.CLASSICAL -> "Classical"
        Genre.GenreType.DANCE -> "Dance"
        Genre.GenreType.ELECTRONIC -> "Electronic"
        Genre.GenreType.METAL -> "Metal"
        Genre.GenreType.RAINY_DAY -> "Rainy day"
        Genre.GenreType.ROCK -> "Rock"
        Genre.GenreType.PIANO -> "Piano"
        Genre.GenreType.POP -> "Pop"
    }
    return Genre(
        id = "${this.name} $ordinal", // TODO
        name = name,
        genreType = genreType
    )
}

/**
 * A utility function used to get the [Genre.GenreType] associated with
 * an enum of type [SupportedSpotifyGenres].
 */
private fun SupportedSpotifyGenres.getGenreType() = when (this) {
    SupportedSpotifyGenres.AMBIENT -> Genre.GenreType.AMBIENT
    SupportedSpotifyGenres.CHILL -> Genre.GenreType.CHILL
    SupportedSpotifyGenres.CLASSICAL -> Genre.GenreType.CLASSICAL
    SupportedSpotifyGenres.DANCE -> Genre.GenreType.DANCE
    SupportedSpotifyGenres.ELECTRONIC -> Genre.GenreType.ELECTRONIC
    SupportedSpotifyGenres.METAL -> Genre.GenreType.METAL
    SupportedSpotifyGenres.RAINY_DAY -> Genre.GenreType.RAINY_DAY
    SupportedSpotifyGenres.ROCK -> Genre.GenreType.ROCK
    SupportedSpotifyGenres.PIANO -> Genre.GenreType.PIANO
    SupportedSpotifyGenres.POP -> Genre.GenreType.POP
    SupportedSpotifyGenres.SLEEP -> TODO()
}

interface SpotifyService {
    @GET(SpotifyEndPoints.SPECIFIC_ARTIST_ENDPOINT)
    suspend fun getArtistInfoWithId(
        @Path("id") artistId: String,
        @Header("Authorization") token: BearerToken,
    ): ArtistDTO

    @GET(SpotifyEndPoints.SPECIFIC_ARTIST_ALBUMS_ENDPOINT)
    suspend fun getAlbumsOfArtistWithId(
        @Path("id") artistId: String,
        @Query("market") market: String,
        @Header("Authorization") token: BearerToken,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("include_groups") includeGroups: String? = null,
    ): AlbumsMetadataDTO

    @GET(SpotifyEndPoints.TOP_TRACKS_ENDPOINT)
    suspend fun getTopTenTracksForArtistWithId(
        @Path("id") artistId: String,
        @Query("market") market: String,
        @Header("Authorization") token: BearerToken
    ): TracksWithAlbumMetadataListDTO

    @GET(SpotifyEndPoints.SPECIFIC_ALBUM_ENDPOINT)
    suspend fun getAlbumWithId(
        @Path("id") albumId: String,
        @Query("market") market: String,
        @Header("Authorization") token: BearerToken
    ): AlbumDTO

    @GET(SpotifyEndPoints.SPECIFIC_PLAYLIST_ENDPOINT)
    suspend fun getPlaylistWithId(
        @Path("playlist_id") playlistId: String,
        @Query("market") market: String,
        @Header("Authorization") token: BearerToken,
        @Query("fields") fields: String = SpotifyEndPoints.Defaults.defaultPlaylistFields
    ): PlaylistDTO

    @GET(SpotifyEndPoints.SEARCH_ENDPOINT)
    suspend fun search(
        @Query("q") searchQuery: String,
        @Query("market") market: String,
        @Header("Authorization") token: BearerToken,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("type") type: String = SpotifyEndPoints.Defaults.defaultSearchQueryTypes,
    ): SearchResultsDTO

    @GET(SpotifyEndPoints.RECOMMENDATIONS_ENDPOINT)
    suspend fun getTracksForGenre(
        @Query("seed_genres") genre: SupportedSpotifyGenres,
        @Query("market") market: String,
        @Header("Authorization") token: BearerToken,
        @Query("limit") limit: Int = 20
    ): TracksWithAlbumMetadataListDTO
}