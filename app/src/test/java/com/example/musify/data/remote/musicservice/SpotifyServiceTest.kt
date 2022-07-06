package com.example.musify.data.remote.musicservice

import com.example.musify.data.dto.AlbumMetadataDTO
import com.example.musify.utils.defaultMusifyJacksonConverterFactory
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class SpotifyServiceTest {
    // artist id of 'Anirudh Ravichander'
    private val validArtistId = "4zCH9qm4R2DADamUHMCa6O"
    lateinit var musicService: SpotifyService

    @Before
    fun setup() {
        val oAuthToken =
            "BQBnbeDCSUOwr8_zJRAoAP0PZUB1uAKJElbINCuAoRKb2zGrb3ldFxvu_0fwSWVeSZMjJrSfWDe64W6kLGjvjvjSUxXFZUQdE-mhSJlEEfYHmtKcKJI"
        val client = OkHttpClient.Builder()
            .addInterceptor(Interceptor { interceptorChain ->
                val request = interceptorChain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $oAuthToken")
                    .build()
                interceptorChain.proceed(request)
            })
            .build()
        musicService = Retrofit.Builder()
            .client(client)
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(defaultMusifyJacksonConverterFactory)
            .build()
            .create(SpotifyService::class.java)
    }

    @Test
    fun getArtistInfoTest_validArtistId_returnsValidArtistDTO() = runBlocking {
        // given a valid artistId
        val artistId = validArtistId
        // the artist must be fetched successfully
        val fetchedArtist = musicService.getArtistInfoWithId(artistId)
        // the id of the fetched artist must match with the 'artistId'
        assert(fetchedArtist.id == artistId)
    }

    @Test(expected = retrofit2.HttpException::class)
    fun getArtistInfoTest_invalidArtistId_returnsNull() {
        // given an invalid artistId
        val artistId = "-"
        // when fetching the artist info
        runBlocking { musicService.getArtistInfoWithId(artistId) }
        // a HttpException must be thrown
    }

    @Test
    fun getAlbumsAssociatedWithArtistTest_validArtistId_returnsAlbumsDTO() {
        // given an valid artistId
        val artistId = validArtistId
        // the albums associated with the artist must be fetched successfully
        runBlocking { musicService.getAlbumsOfArtistWithId(artistId, "IN") }
    }

    @Test
    fun getAlbumsAssociatedWithArtistTest_validArtistIdWithLimit_returnsListOfSpecifiedSize() {
        // given an invalid artistId and limit
        val artistId = validArtistId
        // when fetching the artist with limit set to 10
        val limit = 10
        val albums = runBlocking {
            // the albums associated with the artist must be fetched successfully
            musicService.getAlbumsOfArtistWithId(
                artistId = artistId,
                market = "IN",
                limit = limit
            )
        }
        // the number of items should be equal to the specified limit
        assert(albums.items.size == 10)
    }

    @Test
    fun getTopTracksTest_validArtistId_returnsTopTracksDTO() {
        // given a valid artistId
        val artistId = validArtistId
        runBlocking {
            // the top ten tracks associated with the artist must be
            // successfully fetched
            musicService.getTopTenTracksForArtistWithId(artistId = artistId, market = "IN")
        }
    }

    @Test
    fun getAlbumTest_validAlbumId_returnsAlbumDTO() {
        // given an valid albumId
        val albumId = "4aawyAB9vmqN3uQ7FjRGTy"
        runBlocking {
            // the album must be fetched successfully
            musicService.getAlbumWithId(albumId = albumId, market = "IN")
        }
    }

    @Test
    fun getPlaylistTest_validPlaylistId_returnsPlaylistDTO() {
        // given an valid playlistId
        val albumId = "7sZbq8QGyMnhKPcLJvCUFD"
        runBlocking {
            // the playlist must be fetched successfully
            musicService.getPlaylistWithId(playlistId = albumId, market = "IN")
        }
    }

    @Test
    fun buildSearchQueryTest_listOfSearchQueryEnum_buildsValidSearchQuery() {
        // given a valid list of search query types
        val searchQueryTypes = arrayOf(SearchQueryType.PLAYLIST, SearchQueryType.ARTIST)
        // when building the search query
        val builtQuery = buildSearchQueryWithTypes(*searchQueryTypes)
        // the search query must be a valid one
        assert(builtQuery == "playlist,artist")
    }

    @Test
    fun searchTrackTest_validSearchQueryWithLimit_returnsSearchResultsDTO() {
        // given an valid search query for a track
        val searchQuery = "Visiting hours by Ed Sheeran"
        // when performing search operation
        val searchResultsDTO = runBlocking {
            // the search results must be fetched successfully
            musicService.search(
                searchQuery = searchQuery,
                type = buildSearchQueryWithTypes(SearchQueryType.TRACK),
                market = "IN",
                limit = 1 // limiting the number of results to one
            )
        }
        // all other properties except the 'tracks' property should be null
        with(searchResultsDTO) {
            assert(tracks != null)
            assert(albums == null)
            assert(artists == null)
            assert(playlists == null)
        }
    }

    @Test
    fun searchAlbumTest_validSearchQueryWithLimit_returnsSearchResultsDTO() {
        // given an valid search query for an album
        val searchQuery = "="
        // when performing search operation
        val searchResultsDTO = runBlocking {
            // the search results must be fetched successfully
            musicService.search(
                searchQuery = searchQuery,
                type = buildSearchQueryWithTypes(SearchQueryType.ALBUM),
                market = "IN",
                limit = 1 // limiting the number of results to one
            )
        }
        // all other properties except the 'albums' property should be null
        with(searchResultsDTO) {
            assert(tracks == null)
            assert(albums != null)
            assert(artists == null)
            assert(playlists == null)
        }
    }

    @Test
    fun searchArtistTest_validSearchQueryWithLimit_returnsSearchResultsDTO() {
        // given an valid search query for an artist
        val searchQuery = "ed sheeran"
        // when performing search operation
        val searchResultsDTO = runBlocking {
            // the search results must be fetched successfully
            musicService.search(
                searchQuery = searchQuery,
                type = buildSearchQueryWithTypes(SearchQueryType.ARTIST),
                market = "IN",
                limit = 1 // limiting the number of results to one
            )
        }
        // all other properties except the 'artists' property should be null
        with(searchResultsDTO) {
            assert(tracks == null)
            assert(albums == null)
            assert(artists != null)
            assert(playlists == null)
        }
    }

    @Test
    fun searchPlaylistTest_validSearchQueryWithLimit_returnsSearchResultsDTO() {
        // given an valid search query for a playlist
        val searchQuery = "Anirudh"
        // when performing search operation
        val searchResultsDTO = runBlocking {
            // the search results must be fetched successfully
            musicService.search(
                searchQuery = searchQuery,
                type = buildSearchQueryWithTypes(SearchQueryType.PLAYLIST),
                market = "IN",
                limit = 1 // limiting the number of results to one
            )
        }
        // all other properties except the 'playlists' property should be null
        with(searchResultsDTO) {
            assert(tracks == null)
            assert(albums == null)
            assert(artists == null)
            assert(playlists != null)
        }
    }

    @Test
    fun searchTestForAllTypes_validSearchQueryWithLimit_returnsSearchResultDTO() {
        // given an valid search query
        val searchQuery = "Anirudh"
        // when performing search operation for all types
        val searchResultsDTO = runBlocking {
            // the search results must be fetched successfully
            musicService.search(
                searchQuery = searchQuery,
                market = "IN",
                type = SpotifyEndPoints.Defaults.defaultSearchQueryTypes,
                limit = 1 // limiting the number of results for each type to one
            )
        }
        // all the properties should not be null
        with(searchResultsDTO) {
            assert(tracks != null)
            assert(albums != null)
            assert(artists != null)
            assert(playlists != null)
        }
    }

    /**
     * This test is written to check whether the mapping of a non-null
     * property in a kotlin model class with a null value in json
     * throws [MissingKotlinParameterException] exception. This is because
     * other libraries such as Gson and moshi suppress this discrepancy.
     * In the case of Gson, it just assigns a value of null to a non-null
     * kotlin property, which is disastrous. In the case of Moshi,
     * all properties in the kotlin object are converted to be of
     * nullable type.
     */
    @Test(expected = MissingKotlinParameterException::class)
    fun jacksonDeSerializationTest_nullValueForNonNullableKotlinProperty_throwsException() {
        // given a json string object with a value of non nullable
        // kotlin property set to null
        val jsonString = "{\"id\":null,\"name\":\"s\"}"
        // when mapping the object to a kotlin class with the same field
        // declared as non-nullable
        jacksonObjectMapper().readValue(
            jsonString,
            AlbumMetadataDTO.ArtistInfoDTO::class.java
        )
        // an exception should be thrown
    }
}