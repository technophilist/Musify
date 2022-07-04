package com.example.musify.data.remote.musicservice

import com.example.musify.data.dto.AlbumMetadataDTO
import com.example.musify.data.dto.SearchResultsDTO
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

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
        val jacksonConverterFactory = JacksonConverterFactory.create(
            jacksonObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
            )
        )
        musicService = Retrofit.Builder()
            .client(client)
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(jacksonConverterFactory)
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
        println(response.body())
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
            println(response.body()!!.items.size == 10)
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
        var response: Response<SearchResultsDTO>? = null
        try {
            // given an valid search query for a track
            val searchQuery = "Visiting hours by Ed Sheeran"
            // when performing search operation
            response = musicService.search(
                searchQuery = searchQuery,
                type = buildSearchQueryWithTypes(SearchQueryType.TRACK),
                market = "IN",
                limit = 1 // limiting the number of results to one
            )
            // the body of the response should not be null
        } catch (e: Exception) {
            println(response!!.body() != null)
        }

    }

    @Test
    fun searchAlbumTest_validSearchQueryWithLimit_returnsSearchResultsDTO() = runBlocking {
        // given an valid search query for an album
        val searchQuery = "="
        // when performing search operation
        val response = musicService.search(
            searchQuery = searchQuery,
            type = buildSearchQueryWithTypes(SearchQueryType.ALBUM),
            market = "IN",
            limit = 1 // limiting the number of results to one
        )
        // the body of the response should not be null
        assert(response.body() != null)
    }

    @Test
    fun searchArtistTest_validSearchQueryWithLimit_returnsSearchResultsDTO() = runBlocking {
        // given an valid search query for an artist
        val searchQuery = "ed sheeran"
        // when performing search operation
        val response = musicService.search(
            searchQuery = searchQuery,
            type = buildSearchQueryWithTypes(SearchQueryType.ARTIST),
            market = "IN",
            limit = 1 // limiting the number of results to one
        )
        // the body of the response should not be null
        assert(response.body() != null)
    }

    @Test
    fun searchPlaylistTest_validSearchQueryWithLimit_returnsSearchResultsDTO() = runBlocking {
        // given an valid search query for a playlist
        val searchQuery = "Anirudh"
        // when performing search operation
        val response = musicService.search(
            searchQuery = searchQuery,
            type = buildSearchQueryWithTypes(SearchQueryType.PLAYLIST),
            market = "IN",
            limit = 1 // limiting the number of results to one
        )
        // the body of the response should not be null
        assert(response.body() != null)
    }

    @Test
    fun searchTestForAllTypes_validSearchQueryWithLimit_returnsSearchResultDTO() = runBlocking {
        // given an valid search query
        val searchQuery = "Anirudh"
        // when performing search operation for all types
        val response = musicService.search(
            searchQuery = searchQuery,
            market = "IN",
            type = SpotifyEndPoints.Defaults.defaultSearchQueryTypes,
            limit = 1 // limiting the number of results for each type to one
        )
        // the body of the response should not be null
        assert(response.body() != null)
        // the results for each type should exactly be one
        with(response.body()!!) {
            assert(tracks.value.size == 1)
            assert(albums.value.size == 1)
            assert(artists.value.size == 1)
            assert(playlists.value.size == 1)
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