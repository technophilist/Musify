package com.example.musify.data.remote.musicservice

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SpotifyServiceTest {
    // artist id of 'Anirudh Ravichander'
    private val validArtistId = "4zCH9qm4R2DADamUHMCa6O"
    lateinit var musicService: SpotifyService

    @Before
    fun setup() {
        val oAuthToken =
            "BQC74Ea3XQImXi-C4vlZjSkvI0VxRxqTnYH2zXLGFAZGMpv_bKSoTdh188EAZn-7VrpHCjLRHR-AW1drIwI7ijemtdjCAmjBzgEFmbI55AY-lZybttOKmsGewna1WbqwHG1p3MtxOXD1wMmNDlLgjPkNVwZ-tlyNi0KDEM0d39JInhCPVBbzLjmw-TV1sOagLTM"
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
            .addConverterFactory(GsonConverterFactory.create())
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
        // given an valid search query for a track
        val searchQuery = "Visiting hours by Ed Sheeran"
        // when performing search operation
        val response = musicService.search(
            searchQuery = searchQuery,
            type = buildSearchQueryWithTypes(SearchQueryType.TRACK),
            market = "IN",
            limit = 1 // limiting the number of results to one
        )
        // the body of the response should not be null
        assert(response.body() != null)
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

}