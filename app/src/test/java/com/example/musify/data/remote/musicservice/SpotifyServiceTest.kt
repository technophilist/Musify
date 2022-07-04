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
            "BQAMfnRBpJ8H6x07NCUs0cE5EkYFM6gL8ly407kO-4E9rg8XK_hP0L8lPGKrCiTl7Fu2-L-eW0HNZph7QJtyIHL_OTONuCMdr6YZQ31QA5eosTdXMh7lWvzsMYvuJldtuVYxhOuHq5vms8ZaK-7c9h_7SbhNQSuMZn0M3kgn9cH0zAB51pHSBlpfgUJo1H4khbw"
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
        // when fetching the artist
        val response = musicService.getArtistInfoWithId(artistId)
        // the body of the response shouldn't be null
        assert(response.body() != null)
        // the id of the fetched id must match with the 'artistId'
        assert(response.body()!!.id == artistId)
    }

    @Test
    fun getArtistInfoTest_invalidArtistId_returnsNull() = runBlocking {
        // given an invalid artistId
        val artistId = "-"
        // when fetching the artist
        val response = musicService.getArtistInfoWithId(artistId)
        // the body of the response should be null
        assert(response.body() == null)
    }

    @Test
    fun getAlbumsAssociatedWithArtistTest_validArtistId_returnsAlbumsDTO() = runBlocking {
        // given an invalid artistId
        val artistId = validArtistId
        // when fetching the artist
        val response = musicService.getAlbumsOfArtistWithId(artistId, "IN")
        // the body of the response should not be null
        assert(response.body() != null)
    }

    @Test
    fun getAlbumsAssociatedWithArtistTest_invalidArtistId_returnsNull() = runBlocking {
        // given an invalid artistId
        val artistId = "-"
        // when fetching the artist
        val response = musicService.getAlbumsOfArtistWithId(artistId, "IN")
        // the body of the response should be null
        assert(response.body() == null)
    }

    @Test
    fun getAlbumsAssociatedWithArtistTest_validArtistIdWithLimit_returnsListOfSpecifiedSize() =
        runBlocking {
            // given an invalid artistId and limit
            val artistId = validArtistId
            // when fetching the artist with limit set to 10
            val limit = 10
            val response = musicService.getAlbumsOfArtistWithId(
                artistId = artistId,
                market = "IN",
                limit = limit
            )
            // the body of the response should not be null
            assert(response.body() != null)
            // the number of items should be equal to the specified limit
            assert(response.body()!!.items.size == 10)
        }

    @Test
    fun getTopTracksTest_validArtistId_returnsTopTracksDTO() = runBlocking {
        // given an valid artistId
        val artistId = validArtistId
        // when fetching the top ten tracks
        val response = musicService.getTopTenTracksForArtistWithId(
            artistId = artistId,
            market = "IN"
        )
        // the body of the response should not be null
        assert(response.body() != null)
    }

    @Test
    fun getTopTracksTest_invalidArtistId_returnsNull() = runBlocking {
        // given an invalid artistId and limit
        val artistId = "-"
        // when fetching the top ten tracks
        val response = musicService.getTopTenTracksForArtistWithId(
            artistId = artistId,
            market = "IN"
        )
        // the body of the response should be null
        assert(response.body() == null)
    }

    @Test
    fun getAlbumTest_validAlbumId_returnsAlbumDTO() = runBlocking {
        // given an valid albumId
        val albumId = "4aawyAB9vmqN3uQ7FjRGTy"
        // when fetching the album
        val response = musicService.getAlbumWithId(
            albumId = albumId,
            market = "IN"
        )
        // the body of the response should not be null
        assert(response.body() != null)
    }

    @Test
    fun getAlbumTest_validAlbumId_returnsNull() = runBlocking {
        // given an invalid albumId
        val albumId = "-"
        // when fetching the album
        val response = musicService.getAlbumWithId(
            albumId = albumId,
            market = "IN"
        )
        // the body of the response should  be null
        assert(response.body() == null)
    }

    @Test
    fun getPlaylistTest_validPlaylistId_returnsPlaylistDTO() = runBlocking {
        // given an valid albumId
        val albumId = "7sZbq8QGyMnhKPcLJvCUFD"
        // when fetching the album
        val response = musicService.getPlaylistWithId(
            playlistId = albumId,
            market = "IN"
        )
        // the body of the response should not be null
        assert(response.body() != null)
    }

    @Test
    fun getPlaylistTest_invalidPlaylistId_returnsNull() = runBlocking {
        // given an invalid albumId
        val albumId = "-"
        // when fetching the album
        val response = musicService.getPlaylistWithId(
            playlistId = albumId,
            market = "IN"
        )
        // the body of the response should be null
        assert(response.body() == null)
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
    fun searchTrackTest_validSearchQueryWithLimit_returnsSearchResultsDTO() = runBlocking {

        // given an valid search query for a track
        val searchQuery = "Visiting hours by Ed Sheeran"
        // when performing search operation
        val responseBody = musicService.search(
            searchQuery = searchQuery,
            type = buildSearchQueryWithTypes(SearchQueryType.TRACK),
            market = "IN",
            limit = 1 // limiting the number of results to one
        ).body()
        // the body of the response should not be null
        assert(responseBody != null)
        // all other properties except the 'tracks' property should be null
        with(responseBody!!) {
            assert(tracks != null)
            assert(albums == null)
            assert(artists == null)
            assert(playlists == null)
        }
    }

    @Test
    fun searchAlbumTest_validSearchQueryWithLimit_returnsSearchResultsDTO() = runBlocking {
        // given an valid search query for an album
        val searchQuery = "="
        // when performing search operation
        val responseBody = musicService.search(
            searchQuery = searchQuery,
            type = buildSearchQueryWithTypes(SearchQueryType.ALBUM),
            market = "IN",
            limit = 1 // limiting the number of results to one
        ).body()
        // the body of the response should not be null
        assert(responseBody != null)
        // all other properties except the 'albums' property should be null
        with(responseBody!!) {
            assert(tracks == null)
            assert(albums != null)
            assert(artists == null)
            assert(playlists == null)
        }
    }

    @Test
    fun searchArtistTest_validSearchQueryWithLimit_returnsSearchResultsDTO() = runBlocking {
        // given an valid search query for an artist
        val searchQuery = "ed sheeran"
        // when performing search operation
        val responseBody = musicService.search(
            searchQuery = searchQuery,
            type = buildSearchQueryWithTypes(SearchQueryType.ARTIST),
            market = "IN",
            limit = 1 // limiting the number of results to one
        ).body()
        // the body of the response should not be null
        assert(responseBody != null)
        // all other properties except the 'artists' property should be null
        with(responseBody!!) {
            assert(tracks == null)
            assert(albums == null)
            assert(artists != null)
            assert(playlists == null)
        }
    }

    @Test
    fun searchPlaylistTest_validSearchQueryWithLimit_returnsSearchResultsDTO() = runBlocking {
        // given an valid search query for a playlist
        val searchQuery = "Anirudh"
        // when performing search operation
        val responseBody = musicService.search(
            searchQuery = searchQuery,
            type = buildSearchQueryWithTypes(SearchQueryType.PLAYLIST),
            market = "IN",
            limit = 1 // limiting the number of results to one
        ).body()
        // the body of the response should not be null
        assert(responseBody != null)
        // all other properties except the 'playlists' property should be null
        with(responseBody!!) {
            assert(tracks == null)
            assert(albums == null)
            assert(artists == null)
            assert(playlists != null)
        }
    }

    @Test
    fun searchTestForAllTypes_validSearchQueryWithLimit_returnsSearchResultDTO() = runBlocking {
        // given an valid search query
        val searchQuery = "Anirudh"
        // when performing search operation for all types
        val responseBody = musicService.search(
            searchQuery = searchQuery,
            market = "IN",
            type = SpotifyEndPoints.Defaults.defaultSearchQueryTypes,
            limit = 1 // limiting the number of results for each type to one
        ).body()
        // the body of the response should not be null
        assert(responseBody != null)
        // all the propereties should not be null
        with(responseBody!!) {
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
    fun jacksonDeSerializationTest_nullValueForNonNullableKotlinProperty_throwsException() =
        runBlocking {
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
            return@runBlocking Unit
        }
}