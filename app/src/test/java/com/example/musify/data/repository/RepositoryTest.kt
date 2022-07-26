package com.example.musify.data.repository

import com.example.musify.data.encoder.TestBase64Encoder
import com.example.musify.data.remote.musicservice.SpotifyService
import com.example.musify.data.remote.token.tokenmanager.TokenManager
import com.example.musify.data.repository.tokenrepository.SpotifyTokenRepository
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.domain.Genre
import com.example.musify.domain.MusifyHttpErrorType
import com.example.musify.utils.defaultMusifyJacksonConverterFactory
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class RepositoryTest {

    private lateinit var repository: Repository

    @Before
    fun setup() {
        val spotifyService = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(defaultMusifyJacksonConverterFactory)
            .build()
            .create(SpotifyService::class.java)
        val tokenManager = Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/")
            .addConverterFactory(defaultMusifyJacksonConverterFactory)
            .build()
            .create(TokenManager::class.java)
        repository = MusifyRepository(
            spotifyService,
            SpotifyTokenRepository(
                tokenManager,
                TestBase64Encoder()
            )
        )
    }

    @Test
    fun artistFetchTest_validArtistId_isFetchedSuccessfully() = runBlocking {
        // given a valid artist id
        val validArtistId = "4zCH9qm4R2DADamUHMCa6O" // Anirudh Ravichander
        // when fetching the artist summary using the id
        val resource =
            repository.fetchArtistSummaryForId(validArtistId, MapperImageSize.SMALL)
        // the return type must be of type FetchedResource.Success
        assert(resource is FetchedResource.Success)
    }

    @Test
    fun artistFetchTest_invalidArtistId_returnsFailedFetchedResource() = runBlocking {
        // given an invalid artist id
        val invalid = "-"
        // when fetching the artist summary using the id
        val resource =
            repository.fetchArtistSummaryForId(invalid, MapperImageSize.SMALL)
        // the return type must be of type FetchedResource.Failure
        assert(resource is FetchedResource.Failure)
        // the error type must be MusifyHttpErrorType.INVALID_REQUEST
        assert((resource as FetchedResource.Failure).cause == MusifyHttpErrorType.INVALID_REQUEST)
    }

    @Test
    fun fetchArtistAlbumsTest_validArtistIdAndCountryCode_isFetchedSuccessfully() {
        // given a valid artist id
        val validArtistId = "4zCH9qm4R2DADamUHMCa6O" // Anirudh Ravichander
        val countryCode = "IN"
        // when fetching the albums
        val result = runBlocking {
            repository.fetchAlbumsOfArtistWithId(validArtistId, MapperImageSize.SMALL, countryCode)
        }
        // the return type must be of type FetchedResource.Success
        assert(result is FetchedResource.Success)
        // the list should not be empty
        assert((result as FetchedResource.Success).data.isNotEmpty())
    }

    @Test
    fun fetchArtistAlbumsTest_validArtistIdAndInvalidCountryCode_returnsFailedFetchedResource() {
        // given a valid artist id
        val validArtistId = "4zCH9qm4R2DADamUHMCa6O" // Anirudh Ravichander
        // and an invalid country code
        val countryCode = "0"
        // when fetching the albums
        val result = runBlocking {
            repository.fetchAlbumsOfArtistWithId(validArtistId, MapperImageSize.SMALL, countryCode)
        }
        // the return type must be of type FetchedResource.Failure
        assert(result is FetchedResource.Failure)
        assert((result as FetchedResource.Failure).cause == MusifyHttpErrorType.INVALID_REQUEST)
    }

    @Test
    fun fetchTopTenTracksOfArtist_validArtistId_isSuccessfullyFetched() {
        // given a valid artist id
        val validArtistId = "4zCH9qm4R2DADamUHMCa6O" // Anirudh Ravichander
        val countryCode = "IN"
        // when fetching the top ten tracks of the artist
        val result = runBlocking {
            repository.fetchTopTenTracksForArtistWithId(
                validArtistId,
                MapperImageSize.SMALL,
                countryCode
            )
        }
        // the return type must be of type FetchedResource.Success
        assert(result is FetchedResource.Success)
        // the list should not be empty
        assert((result as FetchedResource.Success).data.isNotEmpty())
    }

    @Test
    fun fetchAlbumTest_validAlbumId_isSuccessfullyFetched() {
        // given an valid albumId
        val albumId = "4aawyAB9vmqN3uQ7FjRGTy"
        // when fetching the album
        val result = runBlocking {
            repository.fetchAlbumWithId(albumId, MapperImageSize.SMALL, "IN")
        }
        // the return type must be of type FetchedResource.Success
        assert(result is FetchedResource.Success)
    }

    @Test
    fun fetchPlaylistTest_validPlaylistId_isSuccessfullyFetched() {
        // (WARNING!: The playlist is a real playlist
        // and it might've been removed by the user)
        // given an valid playlistId
        val playlistId = "7sZbq8QGyMnhKPcLJvCUFD"
        // when fetching the playlist
        val result = runBlocking {
            repository.fetchPlaylistWithId(playlistId, "IN")
        }
        // the return type must be of type FetchedResource.Success
        assert(result is FetchedResource.Success)
    }

    @Test
    fun fetchSearchResultsTest_validSearchQuery_isSuccessfullyFetched() {
        // given an valid search query
        val query = "Dull Knives"
        // when fetching search results for the query
        val result = runBlocking {
            repository.fetchSearchResultsForQuery(query, MapperImageSize.SMALL, "IN")
        }
        // the return type must be of type FetchedResource.Success
        assert(result is FetchedResource.Success)
    }

    @Test
    fun fetchTracksForGenreTest_validGenre_tracksSuccessfullyFetched() {
        val validGenre = Genre(
            id = "",
            name = "",
            genreType = Genre.GenreType.AMBIENT
        )
        val result = runBlocking {
            repository.fetchTracksForGenre(
                validGenre,
                MapperImageSize.SMALL,
                "IN"
            )
        }
        assert(result is FetchedResource.Success)
        assert((result as FetchedResource.Success).data.isNotEmpty())
    }

}