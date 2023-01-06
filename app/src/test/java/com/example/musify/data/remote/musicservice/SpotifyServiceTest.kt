package com.example.musify.data.remote.musicservice

import com.example.musify.data.encoder.TestBase64Encoder
import com.example.musify.data.remote.response.AlbumMetadataResponse
import com.example.musify.data.remote.response.TracksWithAlbumMetadataListResponse
import com.example.musify.data.remote.token.BearerToken
import com.example.musify.data.remote.token.tokenmanager.TokenManager
import com.example.musify.data.repositories.tokenrepository.SpotifyTokenRepository
import com.example.musify.utils.defaultMusifyJacksonConverterFactory
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit

class SpotifyServiceTest {
    // artist id of 'Anirudh Ravichander'
    private val validArtistId = "4zCH9qm4R2DADamUHMCa6O"
    private val tokenRepository = SpotifyTokenRepository(
        Retrofit.Builder().baseUrl(SpotifyBaseUrls.AUTHENTICATION_URL)
            .addConverterFactory(defaultMusifyJacksonConverterFactory).build()
            .create(TokenManager::class.java), TestBase64Encoder()
    )
    lateinit var musicService: SpotifyService

    private fun <R> runBlockingWithToken(block: suspend (BearerToken) -> R): R = runBlocking {
        block(tokenRepository.getValidBearerToken())
    }


    @Before
    fun setup() {
        musicService = Retrofit.Builder().baseUrl("https://api.spotify.com/")
            .addConverterFactory(defaultMusifyJacksonConverterFactory).build()
            .create(SpotifyService::class.java)
    }

    @Test
    fun getArtistInfoTest_validArtistId_returnsValidArtistDTO() {
        // given a valid artistId
        val artistId = validArtistId
        // the artist must be fetched successfully
        val fetchedArtist = runBlockingWithToken { musicService.getArtistInfoWithId(artistId, it) }
        // the id of the fetched artist must match with the 'artistId'
        assert(fetchedArtist.id == artistId)
    }

    @Test(expected = retrofit2.HttpException::class)
    fun getArtistInfoTest_invalidArtistId_returnsNull() {
        // given an invalid artistId
        val artistId = "-"
        // when fetching the artist info
        runBlockingWithToken {
            musicService.getArtistInfoWithId(artistId, it)
        }
        // a HttpException must be thrown
    }

    @Test
    fun getAlbumsAssociatedWithArtistTest_validArtistId_returnsAlbumsDTO() {
        // given an valid artistId
        val artistId = validArtistId
        // the albums associated with the artist must be fetched successfully
        runBlockingWithToken {
            musicService.getAlbumsOfArtistWithId(artistId, "IN", it)
        }
    }

    @Test
    fun getAlbumsAssociatedWithArtistTest_validArtistIdWithLimit_returnsListOfSpecifiedSize() {
        // given an invalid artistId and limit
        val artistId = validArtistId
        // when fetching the artist with limit set to 10
        val limit = 10
        val albums = runBlockingWithToken {
            // the albums associated with the artist must be fetched successfully
            musicService.getAlbumsOfArtistWithId(
                artistId = artistId, market = "IN", limit = limit, token = it
            )
        }
        // the number of items should be equal to the specified limit
        assert(albums.items.size == 10)
    }

    @Test
    fun getTopTracksTest_validArtistId_returnsTopTracksDTO() {
        // given a valid artistId
        val artistId = validArtistId
        runBlockingWithToken {
            // the top ten tracks associated with the artist must be
            // successfully fetched
            musicService.getTopTenTracksForArtistWithId(
                artistId = artistId, market = "IN", token = it
            )
        }
    }

    @Test
    fun getAlbumTest_validAlbumId_returnsAlbumDTO() {
        // given an valid albumId
        val albumId = "4aawyAB9vmqN3uQ7FjRGTy"
        runBlockingWithToken {
            // the album must be fetched successfully
            musicService.getAlbumWithId(albumId = albumId, market = "IN", token = it)
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
        val searchResultsDTO = runBlockingWithToken {
            // the search results must be fetched successfully
            musicService.search(
                searchQuery = searchQuery,
                type = buildSearchQueryWithTypes(SearchQueryType.TRACK),
                market = "IN",
                token = it,
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
        val searchResultsDTO = runBlockingWithToken {
            // the search results must be fetched successfully
            musicService.search(
                searchQuery = searchQuery,
                type = buildSearchQueryWithTypes(SearchQueryType.ALBUM),
                market = "IN",
                token = it,
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
        val searchResultsDTO = runBlockingWithToken {
            // the search results must be fetched successfully
            musicService.search(
                searchQuery = searchQuery,
                type = buildSearchQueryWithTypes(SearchQueryType.ARTIST),
                market = "IN",
                token = it,
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
        val searchResultsDTO = runBlockingWithToken {
            // the search results must be fetched successfully
            musicService.search(
                searchQuery = searchQuery,
                type = buildSearchQueryWithTypes(SearchQueryType.PLAYLIST),
                market = "IN",
                token = it,
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
        val searchResultsDTO = runBlockingWithToken {
            // the search results must be fetched successfully
            musicService.search(
                searchQuery = searchQuery,
                market = "IN",
                type = SpotifyEndPoints.Defaults.defaultSearchQueryTypes,
                token = it,
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
            jsonString, AlbumMetadataResponse.ArtistInfoResponse::class.java
        )
        // an exception should be thrown
    }

    @Test
    fun getTracksForGenreTest_validSupportedGenre_returnsValidResult() {
        val genre = SupportedSpotifyGenres.CHILL
        val tracksWithAlbumMetadataListDTO = runBlockingWithToken {
            musicService.getTracksForGenre(genre, "IN", it)
        }
        assert(tracksWithAlbumMetadataListDTO.value.isNotEmpty())
    }

    @Test
    fun getTracksForGenreTest_allSupportedGenres_listIsNotEmpty() = runBlockingWithToken { token ->
        // given a list of all genres
        val genres = SupportedSpotifyGenres.values().toList()
        val results = mutableListOf<Deferred<TracksWithAlbumMetadataListResponse>>()
        coroutineScope {
            genres.forEach { genre ->
                // when fetching the track list for all genres
                val tracksWithAlbumMetadataListDTO = async {
                    musicService.getTracksForGenre(genre = genre, market = "IN", token = token)
                }
                results.add(tracksWithAlbumMetadataListDTO)
            }
        }
        // each genre must contain at least one track
        assert(results.awaitAll().all { it.value.isNotEmpty() })
    }


    @Test
    fun fetchPlaylistTracksTest_validPlaylistId_returnsTrackListWithoutThrowingException() {
        // Warning, this playlist is a real, user generated playlist, which
        // could be removed/modified/made private in the future, which would
        // cause the test to fail.
        val playlistId = "33PhwTyxEl2c7sn3NE2j2y"
        runBlockingWithToken {
            musicService.getTracksForPlaylist(
                playlistId = playlistId,
                market = "IN",
                token = it,
            )
        }
    }

    @Test
    fun getNewReleasesTest_valid_market_returnsNotEmptyAlbumListSuccessfully() {
        runBlockingWithToken {
            val newlyReleasedAlbums = musicService.getNewReleases(
                token = it, market = "IN"
            ).albums
            assert(newlyReleasedAlbums.items.isNotEmpty())
        }
    }

    @Test
    fun getNewReleasesTest_invalid_market_throws_httpException() {
        runBlockingWithToken {
            try {
                musicService.getNewReleases(
                    token = it, market = "xyz"
                ).albums
            } catch (exception: Exception) {
                assert(exception is HttpException)
            }
        }
    }

    @Test
    fun getFeaturedPlaylistsTest_valid_market_locale_and_timestamp_returnsNonEmptyPlaylistList() {
        runBlockingWithToken {
            val featuredPlaylistsResponse = musicService.getFeaturedPlaylists(
                token = it, market = "IN", locale = "en_IN", timestamp = "2022-08-05T09:00:00"
            )
            assert(featuredPlaylistsResponse.playlists.items.isNotEmpty())
        }
    }

    @Test
    fun getPlaylistsForCategory_valid_categoryId_returnsNonEmptyPlaylistList() {
        runBlockingWithToken {
            val playlistsForSpecificCategoryResponse = musicService.getPlaylistsForCategory(
                token = it, categoryId = "hiphop", market = "US"
            )
            assert(playlistsForSpecificCategoryResponse.playlists.items.isNotEmpty())
        }
    }

    @Test
    fun getBrowseCategoriesTest_valid_market_and_locale_returnsNonEmptyListOfCategories() {
        runBlockingWithToken {
            val browseCategoriesResponse = musicService.getBrowseCategories(
                token = it, market = "US", locale = "en_US"
            )
            assert(browseCategoriesResponse.categories.items.isNotEmpty())
        }
    }

    @Test
    fun searchShowTest_validShowName_returnsAtleastOneShow() {
        runBlockingWithToken {
            val searchResultsDTO = musicService.search(
                searchQuery = "Waveform: The MKBHD Podcast",
                market = "US",
                token = it,
                type = SearchQueryType.SHOW.value
            )
            assert(searchResultsDTO.shows != null)
            assert(searchResultsDTO.shows!!.value.isNotEmpty())
        }
    }

    @Test
    fun searchEpisodeTest_validShowName_returnsAtleastOneEpisode() {
        runBlockingWithToken {
            val searchResultsDTO = musicService.search(
                searchQuery = "Waveform: The MKBHD Podcast",
                market = "US",
                token = it,
                type = SearchQueryType.EPISODE.value
            )
            assert(searchResultsDTO.episodes != null)
            assert(searchResultsDTO.episodes!!.value.isNotEmpty())
        }
    }

    @Test
    fun getEpisodeWithIdTest_validEpisodeId_isFetchedSuccessfully() {
        runBlockingWithToken {
            val validEpisodeId = "5pLYyCItRvIc2SEbuJ3eO8"
            musicService.getEpisodeWithId(token = it, market = "IN", id = validEpisodeId)
        }
    }

    @Test
    fun getShowWithIdTest_validShowId_isFetchedSuccessfully() {
        runBlockingWithToken {
            val validShowId = "6o81QuW22s5m2nfcXWjucc"
            musicService.getShowWithId(
                token = it,
                id = validShowId,
                market = "IN"
            )
        }
    }

    @Test
    fun getEpisodesForShowWithIdTest_validShowId_isFetchedSuccessfully() {
        runBlockingWithToken {
            val validShowId = "6o81QuW22s5m2nfcXWjucc"
            val episodesForShow = musicService.getEpisodesForShowWithId(
                token = it,
                id = validShowId,
                market = "IN",
                limit = 20,
                offset = 0
            )
            assert(episodesForShow.items.isNotEmpty())
            assert(episodesForShow.items.size <= 20)
        }
    }
}